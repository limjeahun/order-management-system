package event.oms.config.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI {
        // http://localhost:8080/swagger-ui.html 로 접속
        val info = Info()
            .title("OMS API Document")
            .version("v1.0.0")
            .description("주문 관리 시스템(OMS) API 명세서")

        val jwtSchemeName = "bearerAuth"
        val securityRequirement = SecurityRequirement().addList(jwtSchemeName) // API에 전역 보안 요구사항 추가
        val components = Components()
            .addSecuritySchemes(jwtSchemeName, SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP) // 타입: HTTP
                .scheme("bearer") // 스킴: bearer
                .bearerFormat("JWT") // 포맷: JWT
            )

        return OpenAPI()
            .components(components)
            .info(info)
            .addSecurityItem(securityRequirement)
    }

    @Bean
    fun ordersGroupOpenAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("orders")
            .pathsToMatch("/api/v1/orders/**")
            .build()
    }

    @Bean
    fun productsGroupOpenAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("products")
            .pathsToMatch("/api/v1/products/**")
            .build()
    }

    @Bean
    fun paymentsGroupOpenAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("payments")
            .pathsToMatch("/api/v1/payments/**")
            .build()
    }

    @Bean
    fun authGroupOpenAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("auth")
            .pathsToMatch("/api/v1/auth/**")
            .build()
    }


}