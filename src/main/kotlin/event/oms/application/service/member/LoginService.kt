package event.oms.application.service.member

import event.oms.adapter.out.security.JwtTokenProvider
import event.oms.application.port.`in`.member.LoginCommand
import event.oms.application.port.`in`.member.LoginUseCase
import event.oms.application.port.`in`.member.TokenInfo
import event.oms.application.port.out.member.ManageTokenPort
import event.oms.application.port.out.security.TokenProviderPort
import event.oms.common.extensions.getLogger
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LoginService(
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val tokenProviderPort           : TokenProviderPort,
    private val manageTokenPort             : ManageTokenPort,
    private val jwtTokenProvider            : JwtTokenProvider, // 만료 시간 참조
): LoginUseCase {
    private val log = getLogger()

    @Transactional
    override fun login(command: LoginCommand): TokenInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(command.username, command.passwordNotEncrypted)
        val authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken)
        SecurityContextHolder.getContext().authentication = authentication
        // AT, RT 생성
        val tokenInfo = tokenProviderPort.generateTokens(authentication)
        log.info("AT, RT 생성 정보 = {}", tokenInfo.toString())
        // RT를 Redis에 저장
        manageTokenPort.saveRefreshToken(
            username = authentication.name,
            refreshToken = tokenInfo.refreshToken,
            expirationMs = jwtTokenProvider.refreshExpirationMs
        )
        return tokenInfo
    }

}