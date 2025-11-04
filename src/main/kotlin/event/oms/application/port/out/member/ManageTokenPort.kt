package event.oms.application.port.out.member

interface ManageTokenPort {
    /**
     * Refresh Token을 Redis에 저장
     */
    fun saveRefreshToken(username: String, refreshToken: String, expirationMs: Long)

    /**
     * (토큰 재발급 시) Refresh Token으로 Username 조회
     */
    fun findRefreshTokenByUsername(username: String): String?

    /**
     * (로그아웃 시) Username을 Key로 Refresh Token을 Redis에서 삭제
     */
    fun deleteRefreshTokenByUsername(username: String)
}