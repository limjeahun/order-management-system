package event.oms.application.port.out.order

import event.oms.domain.model.order.Order


interface SaveOrderPort {
    fun save(order: Order): Order
}