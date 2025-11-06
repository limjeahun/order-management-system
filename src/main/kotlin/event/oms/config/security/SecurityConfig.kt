package event.oms.config.security

import event.oms.adapter.`in`.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .logout { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // 인증 API (모두 허용)
                    .requestMatchers("/api/v1/auth/signup").permitAll()
                    .requestMatchers("/api/v1/auth/login").permitAll()
                    .requestMatchers("/api/v1/auth/refresh").permitAll()
                    // 로그아웃 (인증 필요)
                    .requestMatchers("/api/v1/auth/logout").authenticated()
                    // Swagger (모두 허용)
                    .requestMatchers("/swagger-ui/index.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    // 그 외 모든 요청 (인증 필요)
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        // Swagger UI가 실행되는 오리진(http://localhost:8080) 허용
        configuration.allowedOrigins = listOf("http://localhost:8080")
        // 허용할 HTTP 메서
        configuration.allowedMethods = listOf("*")
        // 허용할 헤더 (모든 헤더 허용)
        configuration.allowedHeaders = listOf("*")
        // 자격 증명(쿠키, Authorization 헤더 등) 허용
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        // 모든 경로("/**")에 대해 위 CORS 설정을 적용
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}