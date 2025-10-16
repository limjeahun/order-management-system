package event.oms.application.port.`in`

import event.oms.domain.model.order.Order

interface OrderUseCase {
    fun order(command: OrderCommand): Order
}