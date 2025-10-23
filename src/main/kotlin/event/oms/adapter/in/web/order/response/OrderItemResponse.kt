package event.oms.adapter.`in`.web.order.response

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

data class OrderItemResponse(
    @Schema(description = "주문한 상품 ID")
    val productId  : Long,
    /*
    @Schema(description = "주문한 상품명")
    val productName: String,
    */
    @Schema(description = "주문 시점의 상품 가격")
    val price      : BigDecimal,
    @Schema(description = "주문 수량")
    val quantity   : Int,
)
