package event.oms.adapter.`in`.kafka

import event.oms.application.port.`in`.order.OrderCommand
import event.oms.application.port.`in`.order.OrderUseCase
import event.oms.application.port.out.order.SendOrderRequestPort
import event.oms.application.port.out.trace.SaveOrderTracePort
import event.oms.common.extensions.getLogger
import org.springframework.kafka.annotation.KafkaListener
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
    fun handleOrderRequest(command: OrderCommand) {
        log.info("Kafka 주문 요청 수신: traceId={}", command.traceId)
        try {
            // 컨슈머가 동기 비즈니스 로직(OrderService) 실행
            val createdOrder = orderUseCase.order(command)
            log.info("주문 처리 성공: traceId={}, orderId={}", command.traceId, createdOrder.id)
        }catch (e: Exception) {
            // 재고 부족(IllegalArgumentException), 동시성 충돌(IllegalStateException) 등
            log.error("주문 처리 실패: traceId={}, error={}. DLQ로 전송합니다.", command.traceId, e.message)
            // 실패한 주문은 DLQ (Dead-Letter Queue)로 전송
            sendOrderRequestPort.sendToDlq(command)
        }
    }
}