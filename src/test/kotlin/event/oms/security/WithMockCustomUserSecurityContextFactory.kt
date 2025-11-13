package event.oms.security

import event.oms.adapter.`in`.security.CustomUserDetails
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

/**
 * @WithMockCustomUser 어노테이션을 실제 SecurityContext로 만들어주는 팩토리
 */
class WithMockCustomUserSecurityContextFactory: WithSecurityContextFactory<WithMockCustomUser> {
    override fun createSecurityContext(annotation: WithMockCustomUser): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()

        val principal = CustomUserDetails(
            id          = annotation.id,
            username    = annotation.username,
            authorities = listOf(SimpleGrantedAuthority(annotation.role))
        )

        val auth = UsernamePasswordAuthenticationToken(
            principal,
            "password",
            principal.authorities
        )

        context.authentication = auth
        return context
    }
}