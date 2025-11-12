package event.oms.application.service.order

import event.oms.application.port.`in`.order.OrderCommand
import event.oms.application.port.`in`.order.RequestOrderUseCase
import event.oms.application.port.out.order.SendOrderRequestPort
import event.oms.application.port.out.trace.OrderTraceResult
import event.oms.application.port.out.trace.SaveOrderTracePort
import event.oms.domain.model.order.OrderTraceStatus
import org.springframework.stereotype.Service

@Service
class RequestOrderService(
    private val sendOrderRequestPort: SendOrderRequestPort,
    private val saveOrderTracePort  : SaveOrderTracePort,
): RequestOrderUseCase {
    /**
     * Kafka로 메시지를 전송하고 traceId를 반환
     */
    override fun requestOrder(command: OrderCommand): String {
        val traceId = command.traceId
        // 1. 포트를 통해 "PROCESSING" 상태 기록
        val status = OrderTraceResult(status = OrderTraceStatus.PROCESSING)
        saveOrderTracePort.save(traceId, command.memberId, status)
        // 2. Kafka로 메시지 전송
        sendOrderRequestPort.send(command)
        // 3. traceId 반환
        return traceId
    }

}