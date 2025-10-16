package event.oms.domain.model.order

import java.math.BigDecimal

class OrderItem(
    val productId: Long,
    val price:     BigDecimal,
    val quantity:  Int
) {
}