package event.oms.application.port.out.payment

import event.oms.domain.model.payment.Payment

interface SavePaymentPort {
    // 결제 정보 저장
    fun save(payment: Payment): Payment
}