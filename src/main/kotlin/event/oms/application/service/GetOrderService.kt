package event.oms.application.service

import event.oms.application.port.`in`.GetOrderQuery
import event.oms.application.port.out.LoadOrderPort
import event.oms.domain.model.order.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetOrderService(
    private val loadOrderPort: LoadOrderPort
) : GetOrderQuery {
    override fun getOrder(orderId: Long): Order? {
        return loadOrderPort.findOrderById(orderId)
    }
}