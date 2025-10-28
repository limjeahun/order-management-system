package event.oms.application.port.`in`.payment

import java.math.BigDecimal

data class ConfirmPaymentCommand(
    val paymentKey: String,
    val orderId   : String,
    val amount    : BigDecimal
)
