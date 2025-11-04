package event.oms.adapter.`in`.web.member.response

import event.oms.application.port.`in`.member.TokenInfo
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "로그인/재발급 응답 (JWT 토큰)")
data class TokenResponse(
    val grantType   : String,
    val accessToken : String,
    val refreshToken: String,
) {
    companion object {
        fun from(tokenInfo: TokenInfo) = TokenResponse(
            grantType    = tokenInfo.grantType,
            accessToken  = tokenInfo.accessToken,
            refreshToken = tokenInfo.refreshToken,
        )
    }
}
