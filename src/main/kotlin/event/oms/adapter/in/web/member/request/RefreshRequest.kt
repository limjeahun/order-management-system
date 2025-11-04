package event.oms.adapter.`in`.web.member.request

import event.oms.application.port.`in`.member.RefreshCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "토큰 재발급 요청")
data class RefreshRequest(
    @field:NotBlank(message = "Refresh Token은 필수입니다.")
    val refreshToken: String
) {
    fun toCommand() = RefreshCommand(refreshToken)
}
