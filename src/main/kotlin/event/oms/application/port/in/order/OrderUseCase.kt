package event.oms.application.port.`in`.order

import event.oms.domain.model.order.Order

interface OrderUseCase {
    fun order(command: OrderCommand): Order
}