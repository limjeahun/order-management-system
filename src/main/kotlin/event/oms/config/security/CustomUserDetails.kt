package event.oms.config.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * 인증된 사용자의 정보 (특히 회원 ID)를 저장
 */
class CustomUserDetails(
    val id: Long, // 도메인 Member ID
    private val username: String,
    private val passwordHash: String? = null,
    private val authorities: Collection<GrantedAuthority>,
): UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
    override fun getPassword(): String? = passwordHash
    override fun getUsername(): String = username
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

}