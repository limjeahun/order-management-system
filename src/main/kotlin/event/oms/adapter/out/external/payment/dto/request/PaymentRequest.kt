package event.oms.adapter.out.external.payment.dto.request

import java.math.BigDecimal

/**
 * 결제 요청 DTO
 */
data class PaymentRequest(
    val orderId: Long,
    val amount : BigDecimal,
)
