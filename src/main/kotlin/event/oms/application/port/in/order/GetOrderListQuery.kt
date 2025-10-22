package event.oms.application.port.`in`.order

import event.oms.domain.model.order.Order

interface GetOrderListQuery {
    fun getAllOrders(memberId: Long): Order
}