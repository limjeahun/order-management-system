package event.oms.adapter.out.external.payment.dto.response

/**
 * 결제 응답 DTO
 */
data class PaymentResponse(
    val paymentId: String,
    val status   : String,
)
