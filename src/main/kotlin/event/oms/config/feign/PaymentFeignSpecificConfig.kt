package event.oms.config.feign

import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean

class PaymentFeignSpecificConfig {
    @Bean
    fun paymentErrorDecoder(): ErrorDecoder {
        return PaymentApiErrorDecoder() // 별도로 구현한 에러 디코더
    }
}