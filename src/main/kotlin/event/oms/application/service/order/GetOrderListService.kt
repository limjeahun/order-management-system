package event.oms.application.service.order

import event.oms.application.port.`in`.order.GetOrderListQuery
import event.oms.application.port.out.order.LoadOrderPort
import event.oms.domain.model.order.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetOrderListService(
    private val loadOrderPort: LoadOrderPort
) : GetOrderListQuery {
    /**
     *
     */
    override fun getAllOrders(memberId: Long): Order {
        TODO("Not yet implemented")
    }
}