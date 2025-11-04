package event.oms.adapter.`in`.web.member.request

import event.oms.application.port.`in`.member.LoginCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "로그인 요청")
data class LoginRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String,
) {
    fun toCommand() = LoginCommand(username, password)
}
