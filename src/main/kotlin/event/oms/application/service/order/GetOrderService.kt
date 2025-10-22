package event.oms.application.service.order

import event.oms.application.port.`in`.order.GetOrderQuery
import event.oms.application.port.out.order.LoadOrderPort
import event.oms.domain.model.order.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetOrderService(
    private val loadOrderPort: LoadOrderPort
) : GetOrderQuery {
    override fun getOrder(orderId: Long): Order {
        return loadOrderPort.findOrderById(orderId)
            ?: throw NoSuchElementException("ID가 ${orderId}인 주문을 찾을 수 없습니다.")
    }
}