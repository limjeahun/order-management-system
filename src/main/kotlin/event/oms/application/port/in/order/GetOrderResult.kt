package event.oms.application.port.`in`.order

import event.oms.domain.model.order.Order

data class GetOrderResult(
    val order: Order,
    val productNames: Map<Long, String>,
) {
    companion object {
        fun from(order: Order, productNames: Map<Long, String>): GetOrderResult {
            return GetOrderResult(
                order        = order,
                productNames = productNames,
            )
        }
    }
}
