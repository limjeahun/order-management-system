package event.oms.adapter.out.external.payment

import event.oms.adapter.out.external.payment.dto.request.TossPaymentApprovalRequest
import event.oms.adapter.out.external.payment.dto.request.TossPaymentRequest
import event.oms.adapter.out.external.payment.dto.response.TossPaymentApprovalResponse
import event.oms.adapter.out.external.payment.dto.response.TossPaymentRequestResponse
import event.oms.config.feign.TossPaymentsFeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "tossPaymentsClient",
    url = "\${external-api.payment.toss}",
    configuration = [TossPaymentsFeignConfig::class] // 특정 설정 클래스 지정
)
interface TossPaymentsClient {

    @PostMapping("/v1/payments")
    fun requestPayment(
        @RequestBody request: TossPaymentRequest
    ): TossPaymentRequestResponse

    @PostMapping("/v1/payments/confirm")
    fun confirmPayment(
        @RequestBody request: TossPaymentApprovalRequest
    ): TossPaymentApprovalResponse

}