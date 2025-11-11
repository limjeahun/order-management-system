package event.oms.config.web

import event.oms.adapter.`in`.web.interceptor.WaitingRoomInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val waitingRoomInterceptor: WaitingRoomInterceptor
): WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(waitingRoomInterceptor)
            .addPathPatterns("/api/v1/orders")
    }

}