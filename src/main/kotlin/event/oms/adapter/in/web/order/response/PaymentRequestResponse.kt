package event.oms.adapter.`in`.web.order.response

import event.oms.application.port.`in`.payment.PaymentRequestResult
import io.swagger.v3.oas.annotations.media.Schema

data class PaymentRequestResponse(
    @Schema(description = "주문 번호")
    val orderId    : Long,
    @Schema(description = "결제키")
    val paymentKey : String,
    @Schema(description = "결제 페이지 URL")
    val redirectUrl: String,
) {
    companion object {
        fun from(result :PaymentRequestResult): PaymentRequestResponse {
            return PaymentRequestResponse(
                result.orderId,
                result.paymentKey,
                result.redirectUrl,
            )
        }

    }
}
