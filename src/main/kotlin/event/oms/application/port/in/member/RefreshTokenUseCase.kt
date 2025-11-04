package event.oms.application.port.`in`.member

interface RefreshTokenUseCase {
    /**
     * Refresh Token을 받아 새로운 Access Token (및 Refresh Token)을 발급
     * @param refreshToken 유효한 Refresh Token
     * @return 새로운 TokenInfo (Access + Refresh)
     */
    fun refreshTokens(command: RefreshCommand): TokenInfo
}