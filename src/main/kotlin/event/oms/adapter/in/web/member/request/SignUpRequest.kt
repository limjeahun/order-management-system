package event.oms.adapter.`in`.web.member.request

import event.oms.application.port.`in`.member.SignUpCommand
import event.oms.domain.model.member.Role
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "회원가입 요청")
data class SignUpRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 20)
    @field:Schema(description = "회원가입 아이디", example = "test")
    val username: String,

    @field:NotBlank
    @field:Size(min = 4, max = 20)
    @field:Schema(description = "회원가입 비밀번호", example = "111111")
    val password: String,

    @field:NotBlank
    @field:Schema(description = "회원 유형", example = "ROLE_USER")
    val role: String,
) {
    fun toCommand() = SignUpCommand(
        username,
        password,
        Role.fromName(role)
    )
}
