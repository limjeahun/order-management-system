package event.oms.application.port.out.member

interface ManageTokenPort {
    /**
     * Refresh Token을 Redis에 저장
     * @param username (Key)
     * @param refreshToken (Value)
     * @param expirationMs 만료 시간
     */
    fun saveRefreshToken(username: String, refreshToken: String, expirationMs: Long)

    /**
     * (로그아웃 시) Username을 Key로 Refresh Token을 Redis에서 삭제
     */
    fun deleteRefreshToken(username: String)

    /**
     * (토큰 재발급 시) Refresh Token으로 Username 조회
     */
    fun findUsernameByRefreshToken(refreshToken: String): String?
}