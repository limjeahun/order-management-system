package event.oms.adapter.out.external.payment

import event.oms.adapter.out.external.payment.dto.request.TossPaymentApprovalRequest
import event.oms.adapter.out.external.payment.dto.request.TossPaymentRequest
import event.oms.adapter.out.external.payment.dto.response.TossPaymentApprovalResponse
import event.oms.adapter.out.external.payment.dto.response.TossPaymentRequestResponse
import event.oms.application.port.out.payment.ConfirmTossPaymentPort
import event.oms.application.port.out.payment.RequestTossPaymentPort
import event.oms.common.extensions.getLogger
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class TossPaymentsAdapter(
    private val tossPaymentsClient: TossPaymentsClient
): RequestTossPaymentPort, ConfirmTossPaymentPort {
    private val log = getLogger()

    /**
     * Toss Payments Request API 요청
     */
    override fun requestTossPayment(
        orderId   : Long,
        orderName : String,
        amount    : BigDecimal,
        successUrl: String,
        failUrl   : String,
    ): TossPaymentRequestResponse {
        log.info("[Toss] Payment Request Start: orderId={}", orderId)
        val request = TossPaymentRequest(
            amount     = amount,
            orderId    = orderId.toString(),
            orderName  = orderName,
            successUrl = successUrl,
            failUrl    = failUrl,
        )

        val response = tossPaymentsClient.requestPayment(request) // Auth header ignored here
        log.info("[Toss] Payment Request Success: orderId={}, paymentKey={}", orderId, response.paymentKey)
        return response
    }

    /**
     * Toss Payments Approval API 요청
     */
    override fun confirmTossPayment(
        paymentKey: String,
        orderId   : String,
        amount    : BigDecimal,
    ): TossPaymentApprovalResponse {
        log.info("[Toss] Payment Confirm Start: orderId={}, paymentKey={}", orderId, paymentKey)
        val request = TossPaymentApprovalRequest(paymentKey, orderId, amount)

        val response = tossPaymentsClient.confirmPayment(request)
        log.info("[Toss] Payment Confirm Success: orderId={}, status={}", orderId, response.status)
        return response
    }


}