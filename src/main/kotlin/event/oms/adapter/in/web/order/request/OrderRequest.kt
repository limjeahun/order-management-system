package event.oms.adapter.`in`.web.order.request

import event.oms.application.port.`in`.OrderCommand
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class OrderRequest(
    @field:NotNull(message = "회원 ID는 필수입니다.")
    val memberId: Long,

    @field:NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다.")
    @field:Valid // 중첩된 객체에 대한 유효성 검사 활성화
    val items: List<OrderItemRequest>,

    @field:NotNull(message = "수령인 정보는 필수입니다.")
    @field:Valid
    val receiverInfo: ReceiverInfoRequest,

) {
    fun toCommand(): OrderCommand {
        return OrderCommand(
            memberId     = this.memberId,
            items        = this.items.map { it.toCommand() },
            receiverInfo = this.receiverInfo.toCommand(),
        )
    }
}
