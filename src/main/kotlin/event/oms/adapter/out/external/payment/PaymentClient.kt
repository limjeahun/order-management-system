package event.oms.adapter.out.external.payment

import event.oms.adapter.out.external.payment.dto.request.PaymentRequest
import event.oms.adapter.out.external.payment.dto.response.PaymentResponse
import event.oms.config.feign.PaymentFeignSpecificConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "paymentClient",
    url = "\${external-api.payment.url}",
    configuration = [PaymentFeignSpecificConfig::class] // 특정 설정 클래스 지정
)
interface PaymentClient {
    @PostMapping("/payment")
    fun processPayment(@RequestBody request: PaymentRequest): PaymentResponse

}