package event.oms.application.port.`in`.member

/**
 * JWT 토큰 정보 (Access + Refresh)
 */
data class TokenInfo(
    val grantType   : String,
    val accessToken : String,
    val refreshToken: String,
)
