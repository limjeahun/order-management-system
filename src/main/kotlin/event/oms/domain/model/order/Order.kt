package event.oms.domain.model.order

import java.time.LocalDateTime

class Order(
    val id: Long? = null,
    val memberId:   Long,
    val orderItems: List<OrderItem>,
    var status:     OrderStatus,
    val orderDate:  LocalDateTime
) {
}