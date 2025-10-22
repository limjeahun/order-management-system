package event.oms.adapter.`in`.web.order.request

import event.oms.application.port.`in`.order.ReceiverInfoCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty

@Schema(description = "주문수신 요청 정보")
data class ReceiverInfoRequest(
    @field:NotEmpty
    val name: String,
    @field:NotEmpty
    val phone: String,
    @field:NotEmpty
    val address: String,
) {
    fun toCommand() = ReceiverInfoCommand(name, phone, address)
}
