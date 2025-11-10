package event.oms.application.service.order

import event.oms.application.port.`in`.order.OrderCommand
import event.oms.application.port.`in`.order.RequestOrderUseCase
import org.springframework.stereotype.Service

@Service
class RequestOrderService(

): RequestOrderUseCase {
    override fun requestOrder(command: OrderCommand): String {
        TODO("Not yet implemented")
    }

}