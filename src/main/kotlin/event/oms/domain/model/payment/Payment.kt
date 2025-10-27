package event.oms.domain.model.payment

import java.math.BigDecimal
import java.time.LocalDateTime

class Payment(
    val id         : Long? = null,
    val orderId    : Long,
    val paymentKey : String,
    val amount     : BigDecimal,
    var status     : PaymentStatus,
    val requestedAt: LocalDateTime,
    var approvedAt : LocalDateTime,
) {

}