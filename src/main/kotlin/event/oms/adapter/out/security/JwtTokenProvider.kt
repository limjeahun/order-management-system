package event.oms.adapter.out.security


import event.oms.application.port.`in`.member.TokenInfo
import event.oms.application.port.out.security.TokenProviderPort
import event.oms.adapter.`in`.security.CustomUserDetails
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    secretKey: String,
    @Value("\${jwt.access-expiration-ms}")
    val accessExpirationMs: Long,
    @Value("\${jwt.refresh-expiration-ms}")
    val refreshExpirationMs: Long,
): TokenProviderPort {
    private val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    companion object {
        private const val AUTHORITIES_KEY = "auth"
    }

    /**
     * Outbound Port 구현: Access Token과 Refresh Token 동시 생성
     */
    override fun generateTokens(authentication: Authentication): TokenInfo {
        val accessToken = createAccessToken(authentication)
        val refreshToken = createRefreshToken(authentication)
        return TokenInfo(
            grantType = "Bearer",
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    /**
     * 1. Access Token 생성 (짧은 만료 시간, 권한 정보 포함)
     */
    private fun createAccessToken(authentication: Authentication): String {
        val authorities: String = authentication.authorities.joinToString(",") { it.authority }
        val principal = authentication.principal as CustomUserDetails
        val now = Date()
        val expirationDate = Date(now.time + accessExpirationMs)

        return Jwts.builder()
            .subject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .claim("memberId", principal.id)
            .issuedAt(now)
            .expiration(expirationDate)
            .signWith(key)
            .compact()
    }

    /**
     * 2. Refresh Token 생성 (긴 만료 시간, 최소 정보)
     */
    private fun createRefreshToken(authentication: Authentication): String {
        val now = Date()
        val expirationDate = Date(now.time + refreshExpirationMs)
        return Jwts.builder()
            .subject(authentication.name)
            .issuedAt(now)
            .expiration(expirationDate)
            .signWith(key)
            .compact()
    }

    // (Access Token 검증용)
    fun validateToken(token: String): Boolean {
        try {
            parseClaims(token)
            return true
        } catch (e: Exception) {
            // (로그) MalformedJwtException, ExpiredJwtException 등
            return false
        }
    }

    // (Access Token 파싱용)
    fun parseClaims(accessToken: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(accessToken)
            .payload
    }

    // (Access Token 인증 객체 생성용)
    fun getAuthentication(accessToken: String): Authentication {
        val claims: Claims = parseClaims(accessToken)
        val authorities: Collection<GrantedAuthority> =
            (claims[AUTHORITIES_KEY]?.toString() ?: "")
                .split(",")
                .filter { it.isNotEmpty() }
                .map { SimpleGrantedAuthority(it) }

        val memberId = claims["memberId"]?.toString()?.toLong()
            ?: throw RuntimeException("Token does not contain memberId")

        val principal = CustomUserDetails(
            id = memberId,
            username = claims.subject,
            authorities = authorities
        )
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }



}