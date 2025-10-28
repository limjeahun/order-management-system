package event.oms.adapter.out.external.payment.dto.response

import java.math.BigDecimal

data class TossPaymentApprovalResponse(
    val paymentKey : String,
    val orderId    : String,
    val status     : String,
    val totalAmount: BigDecimal,
    val approvedAt : String,
)
