package event.oms.adapter.`in`.web.member.request

import event.oms.application.port.`in`.member.SignUpCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "회원가입 요청")
data class SignUpRequest(
    @field:NotBlank @field:Size(min = 4, max = 20) val username: String,
    @field:NotBlank @field:Size(min = 6, max = 20) val password: String,
) {
    fun toCommand() = SignUpCommand(username, password)
}
