package event.oms.adapter.`in`.web.order.request

import event.oms.application.port.`in`.order.OrderItemCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import org.jetbrains.annotations.NotNull

@Schema(description = "주문제품 요청 정보")
data class OrderItemRequest(
    @field:NotNull
    val productId: Long,

    @field:Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    val quantity: Int
) {
    fun toCommand() = OrderItemCommand(productId, quantity)
}
