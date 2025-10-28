package event.oms.adapter.out.external.payment.dto.response

data class TossPaymentRequestResponse(
    val paymentKey: String,
    val orderId   : String,
    val status    : String,
    val checkout: TossCheckoutInfo?,
)

data class TossCheckoutInfo(
    val url: String // Redirect URL for user
)
