package event.oms.application.port.out.security

import event.oms.application.port.`in`.member.TokenInfo
import org.springframework.security.core.Authentication

interface TokenProviderPort {
    /**
     * 인증 정보를 기반으로 Access Token과 Refresh Token을 모두 생성
     */
    fun generateTokens(authentication: Authentication): TokenInfo
}