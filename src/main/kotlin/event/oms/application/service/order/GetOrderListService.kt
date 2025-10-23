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
     * 회원주문 목록 조회
     */
    override fun getAllOrdersByMember(memberId: Long): List<Order> {
        return loadOrderPort.findAllByMemberId(memberId)

    }
}