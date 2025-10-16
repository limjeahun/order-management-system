package event.oms.application.port.out

import event.oms.domain.model.order.Order

interface LoadOrderPort {

    fun findOrderById(orderId: Long): Order?
}