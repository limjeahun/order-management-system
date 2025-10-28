package event.oms.adapter.out.persistence.payment.mapper

import event.oms.adapter.out.persistence.payment.entity.PaymentEntity
import event.oms.domain.model.payment.Payment

/**
 * 도메인 Payment 객체를 JPA PaymentEntity 객체 변환
 */
fun Payment.toEntity(): PaymentEntity {
    return PaymentEntity(
        id          = id,
        orderId     = orderId,
        paymentKey  = paymentKey,
        amount      = amount,
        status      = status,
        requestedAt = requestedAt,
        approvedAt  = approvedAt,

    )
}

/**
 * JPA PaymentEntity 객체를 도메인 Payment 객체로 변환
 */
fun PaymentEntity.toDomain(): Payment {
    return Payment(
        id          = id,
        orderId     = orderId,
        paymentKey  = paymentKey,
        amount      = amount,
        status      = status,
        requestedAt = requestedAt,
        approvedAt  = approvedAt,
    )
}