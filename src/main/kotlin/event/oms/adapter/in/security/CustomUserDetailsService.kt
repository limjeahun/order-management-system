package event.oms.adapter.`in`.security

import event.oms.application.port.`in`.member.GetMemberQuery
import event.oms.common.extensions.getLogger
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class CustomUserDetailsService(
    private val getMemberQuery: GetMemberQuery // DB/Cache 조회용 Port
): UserDetailsService {
    private val log = getLogger()
    /**
     * 로그인 시 Spring Security가 호출
     */
    override fun loadUserByUsername(username: String): UserDetails {
        // Application Service 호출
        val member = getMemberQuery.getMemberByUsername(username)
        val authorities = listOf(SimpleGrantedAuthority(member.role.name))
        // 인증 객체 생성 시 DB의 PasswordHash를 사용 (DB 비밀번호를 직접 반환)
        log.info("인증회원 정보 = {}", member.toString())
        
        return CustomUserDetails(
            id = member.id?: throw NoSuchElementException("잘못된 인증 정보입니다."),
            member.username,
            member.passwordHash, // DB에 저장된 해시된 비밀번호
            authorities,
        )
    }


}