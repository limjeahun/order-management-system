package event.oms.adapter.out.external.payment.dto.request

import java.math.BigDecimal

data class TossPaymentRequest(
    val amount    : BigDecimal,
    val orderId   : String,
    val orderName : String,
    val successUrl: String,
    val failUrl   : String,
)
