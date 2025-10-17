package event.oms.adapter.`in`.web.order.request

import event.oms.application.port.`in`.order.ReceiverInfoCommand
import jakarta.validation.constraints.NotEmpty

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
