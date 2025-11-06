package event.oms.config.security

import event.oms.application.port.out.member.LoadMemberPort
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val loadMemberPort: LoadMemberPort // DB/Cache 조회용 Port
): UserDetailsService {
    /**
     * 로그인 시 Spring Security가 호출
     */
    override fun loadUserByUsername(username: String): UserDetails {
        // @Cacheable 동작
        val member = loadMemberPort.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        val authorities = listOf(SimpleGrantedAuthority(member.role.name))
        // 인증 객체 생성 시 DB의 PasswordHash를 사용 (DB 비밀번호를 직접 반환)
        return CustomUserDetails(
            id = member.id?: throw NoSuchElementException("잘못된 인증 정보입니다."),
            member.username,
            member.passwordHash, // [중요] DB에 저장된 해시된 비밀번호
            authorities
        )
    }


}