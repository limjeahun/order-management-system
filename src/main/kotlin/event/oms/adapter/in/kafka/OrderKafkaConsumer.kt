package event.oms.adapter.`in`.kafka

import event.oms.application.port.`in`.order.OrderCommand
import event.oms.application.port.`in`.order.OrderUseCase
import event.oms.application.port.out.order.SendOrderRequestPort
import event.oms.application.port.out.trace.OrderTraceResult
import event.oms.application.port.out.trace.SaveOrderTracePort
import event.oms.common.extensions.getLogger
import event.oms.domain.model.order.OrderTraceStatus
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class OrderKafkaConsumer(
    private val orderUseCase        : OrderUseCase, // 기존 동기 주문 서비스 주입
    private val sendOrderRequestPort: SendOrderRequestPort, // DLQ 전송용
    private val saveOrderTracePort  : SaveOrderTracePort,
) {
    private val log = getLogger()

    @KafkaListener(
        topics = ["\${promotion.order-topic}"],
        groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun handleOrderRequest(
        // Nullable 페이로드: 역직렬화 실패 시 null
        @Payload(required = false) command: OrderCommand?,
        // 실패 시 예외 객체를 헤더로 받음
        @Header("spring.deserializer.exception.value", required = false)
        deserializationException: Exception?,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int
    ) {
        // '포이즌 필' (역직렬화 실패) 감지
        if (command == null) {
            log.error(
                "Kafka 메시지 역직렬화 실패 (Poison Pill). Offset을 스킵합니다. " +
                        "Partition: {}, Offset: {}, Error: {}",
                partition,
                offset,
                deserializationException?.message
            )
            return
        }

        log.info("Kafka 주문 요청 수신: traceId={}", command.traceId)
        try {
            // 1. 주문 비즈니스 로직 실행
            val createdOrder = orderUseCase.order(command)
            log.info("주문 처리 성공: traceId={}, orderId={}", command.traceId, createdOrder.id)
            // 2. 성공: 포트를 통해 "COMPLETED" 및 "orderId" 기록
            val status = OrderTraceResult(
                status  = OrderTraceStatus.COMPLETED,
                orderId = createdOrder.id
            )
            saveOrderTracePort.save(command.traceId, command.memberId, status)
        }catch (e: Exception) {
            // 재고 부족(IllegalArgumentException), 동시성 충돌(IllegalStateException) 등
            log.error("주문 처리 실패: traceId={}, error={}. DLQ로 전송합니다.", command.traceId, e.message)
            // 실패: 포트를 통해 "FAILED" 기록
            val status = OrderTraceResult(status = OrderTraceStatus.FAILED)
            saveOrderTracePort.save(command.traceId, command.memberId, status)
            // 실패한 주문은 DLQ (Dead-Letter Queue)로 전송
            sendOrderRequestPort.sendToDlq(command)
        }
    }
}