package event.oms.adapter.out.persistence.payment.entity

import event.oms.domain.model.payment.PaymentStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "payment")
class PaymentEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id          : Long? = null,

    @Column(nullable = false, unique = true)
    val orderId: Long,

    @Column(nullable = false, unique = true)
    val paymentKey: String,

    @Column(nullable = false)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PaymentStatus,

    @Column(nullable = false)
    val requestedAt: LocalDateTime,

    var approvedAt: LocalDateTime,
) {

}