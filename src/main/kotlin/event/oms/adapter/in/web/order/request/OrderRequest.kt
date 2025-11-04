package event.oms.adapter.`in`.web.order.request

import event.oms.application.port.`in`.order.OrderCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

@Schema(description = "주문 요청 정보")
data class OrderRequest(
    @field:NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다.")
    @field:Valid // 중첩된 객체에 대한 유효성 검사 활성화
    val items: List<OrderItemRequest>,

    @field:NotNull(message = "수령인 정보는 필수입니다.")
    @field:Valid
    val receiverInfo: ReceiverInfoRequest,

) {
    fun toCommand(memberId: Long): OrderCommand {
        return OrderCommand(
            memberId     = memberId,
            items        = this.items.map { it.toCommand() },
            receiverInfo = this.receiverInfo.toCommand(),
        )
    }
}
