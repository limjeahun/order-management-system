package event.oms.adapter.`in`.web.order.response

import java.math.BigDecimal

data class OrderItemResponse(
    val productId: Long,
    val price    : BigDecimal,
    val quantity : Int,
)
