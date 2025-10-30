package event.oms.application.port.out.payment

import event.oms.adapter.out.external.payment.dto.response.TossPaymentApprovalResponse
import java.math.BigDecimal

interface ConfirmTossPaymentPort {
    /**
     * Toss Payments Approval API 요청
     * @return oss Payments Approval API 응답
     */
    fun confirmTossPayment(paymentKey: String, orderId: String, amount: BigDecimal): TossPaymentApprovalResponse
}