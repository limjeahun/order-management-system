package event.oms.adapter.out.persistence.payment

import event.oms.adapter.out.persistence.payment.mapper.toDomain
import event.oms.adapter.out.persistence.payment.mapper.toEntity
import event.oms.adapter.out.persistence.payment.repository.PaymentJpaRepository
import event.oms.application.port.out.payment.PaymentPersistencePort
import event.oms.domain.model.payment.Payment
import org.springframework.stereotype.Component

@Component
class PaymentPersistenceAdapter(
    private val repository: PaymentJpaRepository,
): PaymentPersistencePort {
    override fun save(payment: Payment): Payment {
        return repository.save(
            payment.toEntity()
        ).toDomain()
    }

    override fun findByOrderId(orderId: Long): Payment? {
        return repository.findByOrderId(orderId)?.toDomain()
    }

    override fun findByPaymentKey(paymentKey: String): Payment? {
        return repository.findByPaymentKey(paymentKey)?.toDomain()
    }


}