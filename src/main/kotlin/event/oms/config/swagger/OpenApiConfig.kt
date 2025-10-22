package event.oms.config.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
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
        return OpenAPI()
            .components(Components()) // 스키마 등 공통 컴포넌트 설정 (필요시)
            .info(info)               // 위에서 정의한 Info 객체 설정
    }

    @Bean
    fun groupOpenAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("OMS")
            .pathsToMatch("/api/v1/**")
            .build()
    }

}