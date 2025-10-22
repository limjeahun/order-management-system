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
            .components(Components())
            .info(info)
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

}