package event.oms.adapter.`in`.web.order.response

import event.oms.domain.model.order.Order
import event.oms.domain.model.order.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse(
    val orderId   : Long,
    val memberId  : Long,
    val status    : OrderStatus,
    val orderDate : LocalDateTime,
    val totalPrice: BigDecimal,
    val items     : List<OrderItemResponse>,
) {
    companion object {
        fun from(order: Order): OrderResponse {
            return OrderResponse(
                orderId    = order.id!!,
                memberId   = order.memberId,
                status     = order.status,
                orderDate  = order.orderDate,
                totalPrice = order.orderItems.sumOf { it.price.multiply(BigDecimal.valueOf(it.quantity.toLong()))},
                items      = order.orderItems.map { OrderItemResponse(it.productId, it.price, it.quantity) }
            )
        }

    }
}
