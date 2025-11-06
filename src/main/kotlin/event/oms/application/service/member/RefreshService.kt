package event.oms.application.service.member

import event.oms.adapter.out.security.JwtTokenProvider
import event.oms.application.port.`in`.member.RefreshCommand
import event.oms.application.port.`in`.member.RefreshTokenUseCase
import event.oms.application.port.`in`.member.TokenInfo
import event.oms.application.port.out.member.LoadMemberPort
import event.oms.application.port.out.member.ManageTokenPort
import event.oms.application.port.out.security.TokenProviderPort
import event.oms.adapter.`in`.security.CustomUserDetails
import event.oms.domain.model.member.Member
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RefreshService(
    private val manageTokenPort  : ManageTokenPort,
    private val jwtTokenProvider : JwtTokenProvider, // validateToken을 위해 구체 클래스 주입
    private val tokenProviderPort: TokenProviderPort,
    private val loadMemberPort   : LoadMemberPort,
): RefreshTokenUseCase {
    /**
     *
     */
    @Transactional
    override fun refreshTokens(command: RefreshCommand): TokenInfo {
        val refreshToken = command.refreshToken
        // 1. RT 서명 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw IllegalArgumentException("유효하지 않은 Refresh Token 서명입니다.")
        }

        // 2. RT에서 Username 추출
        val username = jwtTokenProvider.parseClaims(refreshToken).subject
            ?: throw IllegalArgumentException("Refresh Token에 username이 없습니다.")

        // 3. Redis에 저장된 RT와 일치하는지 (Stateful) 검증
        val storedRt = manageTokenPort.findRefreshTokenByUsername(username)
        if (storedRt != refreshToken) {
            throw IllegalArgumentException("로그아웃되었거나 탈취된 Refresh Token입니다.")
        }

        // 4. 새 토큰 발급을 위해 사용자 정보 조회 (DB or Cache)
        val member = loadMemberPort.findByUsername(username)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다: $username")

        // 5. 새 인증 객체 생성
        val authentication = createAuthenticationFromMember(member)

        // 6. [토큰 회전] 새로운 AT, RT 발급
        val newTokenInfo = tokenProviderPort.generateTokens(authentication)

        // 7. [Stateful] 새로 발급한 RT를 Redis에 덮어쓰기
        manageTokenPort.saveRefreshToken(
            username = username,
            refreshToken = newTokenInfo.refreshToken,
            expirationMs = jwtTokenProvider.refreshExpirationMs
        )
        return newTokenInfo
    }

    /**
     *
     */
    private fun createAuthenticationFromMember(member: Member): Authentication {
        val authorities = listOf(SimpleGrantedAuthority(member.role.name))
        val principal = CustomUserDetails(
            id = member.id!!,
            username = member.username,
            authorities = authorities
        )
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }
}