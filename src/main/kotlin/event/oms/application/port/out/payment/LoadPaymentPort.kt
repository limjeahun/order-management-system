package event.oms.application.port.out.payment

import event.oms.domain.model.payment.Payment

interface LoadPaymentPort {
    fun findByOrderId(orderId: Long): Payment?
    fun findByPaymentKey(paymentKey: String): Payment?
}