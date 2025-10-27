package event.oms.adapter.out.persistence.payment.mapper

import event.oms.adapter.out.persistence.payment.entity.PaymentEntity
import event.oms.domain.model.payment.Payment

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