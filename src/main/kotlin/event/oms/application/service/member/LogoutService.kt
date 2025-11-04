package event.oms.application.service.member

import event.oms.application.port.`in`.member.LogoutUseCase
import event.oms.application.port.out.member.ManageTokenPort
import org.springframework.stereotype.Service

@Service
class LogoutService(
    private val manageTokenPort: ManageTokenPort
): LogoutUseCase {
    /**
     * @param username 대신 로그아웃할 사용자의 'username'을 받음 (인증 객체에서)
     */
    override fun logout(username: String) {
        // Redis에서 RT 삭제
        manageTokenPort.deleteRefreshTokenByUsername(username)
    }
}