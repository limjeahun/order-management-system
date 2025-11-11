package event.oms.application.service.order

import event.oms.application.port.`in`.order.OrderCommand
import event.oms.application.port.`in`.order.RequestOrderUseCase
import event.oms.application.port.out.order.SendOrderRequestPort
import event.oms.application.port.out.trace.OrderTraceStatus
import event.oms.application.port.out.trace.SaveOrderTracePort
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

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
        val status = OrderTraceStatus(status = "PROCESSING")
        saveOrderTracePort.save(traceId, status)

        // 2. [!] Kafka로 메시지 전송
        sendOrderRequestPort.send(command)

        // 3. [!] traceId 반환
        return traceId
    }

}