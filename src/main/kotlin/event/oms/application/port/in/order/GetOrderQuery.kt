package event.oms.application.port.`in`.order

import event.oms.domain.model.order.Order

interface GetOrderQuery {
    fun getOrder(orderId: Long): Order
}