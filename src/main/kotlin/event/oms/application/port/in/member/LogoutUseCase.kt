package event.oms.application.port.`in`.member

interface LogoutUseCase {
    /**
     * 인증된 사용자의 Refresh Token을 무효화 (로그아웃)
     * @param username 로그아웃할 사용자 ID
     */
    fun logout(username: String)
}