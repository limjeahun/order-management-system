package event.oms.config.feign

import feign.Logger
import feign.RequestInterceptor
import feign.RequestTemplate
import feign.codec.ErrorDecoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import java.util.Base64

class TossPaymentsFeignConfig {

    @Value("\${toss.payments.secret-key}")
    private lateinit var secretKey: String

    @Bean
    fun tossAuthRequestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template: RequestTemplate ->
            val authKey = Base64.getEncoder().encodeToString((secretKey + ":").toByteArray())
            template.header("Authorization", "Basic $authKey")
            template.header("Content-Type", "application/json")
        }
    }

    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.FULL
    }

    @Bean
    fun paymentErrorDecoder(): ErrorDecoder {
        return PaymentApiErrorDecoder()
    }


}