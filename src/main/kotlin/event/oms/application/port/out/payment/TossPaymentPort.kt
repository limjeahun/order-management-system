package event.oms.application.port.out.payment

import event.oms.adapter.out.external.payment.dto.response.TossPaymentApprovalResponse
import event.oms.adapter.out.external.payment.dto.response.TossPaymentRequestResponse
import java.math.BigDecimal

interface TossPaymentPort {
    /**
     * Toss Payments Request API 요청
     * @return Toss Payments Request API 응답
     */
    fun requestTossPayment(orderId: Long, orderName: String, amount: BigDecimal, successUrl: String, failUrl: String): TossPaymentRequestResponse

    /**
     * Toss Payments Approval API 요청
     * @return oss Payments Approval API 응답
     */
    fun confirmTossPayment(paymentKey: String, orderId: String, amount: BigDecimal): TossPaymentApprovalResponse
}