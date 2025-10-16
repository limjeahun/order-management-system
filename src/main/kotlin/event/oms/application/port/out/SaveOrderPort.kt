package event.oms.application.port.out

import event.oms.domain.model.order.Order


interface SaveOrderPort {
    fun save(order: Order): Order
}