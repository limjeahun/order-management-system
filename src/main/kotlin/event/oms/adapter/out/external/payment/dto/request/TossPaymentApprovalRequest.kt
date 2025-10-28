package event.oms.adapter.out.external.payment.dto.request

import java.math.BigDecimal

data class TossPaymentApprovalRequest(
    val paymentKey: String,
    val orderId   : String,
    val amount    : BigDecimal,
)
