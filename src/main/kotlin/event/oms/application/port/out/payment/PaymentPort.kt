package event.oms.application.port.out.payment

import java.math.BigDecimal

interface PaymentPort {
    fun requestPayment(orderId: Long, amount: BigDecimal): String // 결제 ID 반환
}