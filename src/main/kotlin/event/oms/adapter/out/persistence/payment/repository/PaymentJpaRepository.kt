package event.oms.adapter.out.persistence.payment.repository

import event.oms.adapter.out.persistence.payment.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentJpaRepository: JpaRepository<PaymentEntity, Long> {
    fun findByOrderId(orderId: Long): PaymentEntity?
    fun findByPaymentKey(paymentKey: String): PaymentEntity?
}