package event.oms.application.port.`in`.payment

data class PaymentRequestResult(
    val orderId    : Long,
    val paymentKey : String,
    val redirectUrl: String,
)
