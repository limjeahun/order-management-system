package event.oms.adapter.`in`.web.member.request

import event.oms.application.port.`in`.member.LoginCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "로그인 요청")
data class LoginRequest(
    @field:Schema(description = "회원아이디", example = "armada")
    @field:NotBlank(message = "회원아이디를 입력해 주세요.")
    val username: String,

    @field:Schema(description = "회원 비밀번호", example = "111111")
    @field:NotBlank(message = "회원 비밀번호를 입력해 주세요.")
    val password: String,
) {
    fun toCommand() = LoginCommand(username, password)
}
