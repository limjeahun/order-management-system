package event.oms.application.port.out.order

import event.oms.domain.model.order.Order

interface LoadOrderPort {

    fun findOrderById(orderId: Long): Order?

    fun findAllByMemberId(memberId: Long): List<Order>
}