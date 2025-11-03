package event.oms.config.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Spring Security의 UserDetails 구현체
 * 인증된 사용자의 정보 (특히 회원 ID)를 저장하는 데 사용됩니다.
 */
class CustomUserDetails(
    val id: Long, // 도메인 Member ID
    private val username: String,
    private val authorities: Collection<GrantedAuthority>,
): UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
    override fun getPassword(): String? = null // 비밀번호는 저장하지 않음
    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

}