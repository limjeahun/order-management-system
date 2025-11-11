package event.oms.application.service.order

import event.oms.application.port.`in`.order.OrderCommand
import event.oms.application.port.`in`.order.RequestOrderUseCase
import event.oms.application.port.out.order.SendOrderRequestPort
import org.springframework.stereotype.Service

@Service
class RequestOrderService(
    private val sendOrderRequestPort: SendOrderRequestPort
): RequestOrderUseCase {
    /**
     * Kafka로 메시지를 전송하고 traceId를 반환
     */
    override fun requestOrder(command: OrderCommand): String {
        sendOrderRequestPort.send(command)
        return command.traceId
    }

}