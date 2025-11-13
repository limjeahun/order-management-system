package event.oms.security

import org.springframework.security.test.context.support.WithSecurityContext

/**
 * 테스트용 CustomUserDetails를 SecurityContext에 주입하는 어노테이션
 */
@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory::class)
annotation class WithMockCustomUser(
    val id: Long = 1L, // 기본 회원 ID
    val username: String = "armada",
    val role: String = "ROLE_USER"
)
