package event.oms.adapter.out.external.payment

import event.oms.adapter.out.external.payment.dto.request.PaymentRequest
import event.oms.application.port.out.payment.PaymentPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class PaymentAdapter(
    private val paymentClient: PaymentClient,
): PaymentPort {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 결제 API 요청
     */
    override fun requestPayment(orderId: Long, amount: BigDecimal): String {
        log.info("[Payment] 요청 시작: orderId={}, amount={}", orderId, amount)
        try {
            val request  = PaymentRequest(orderId, amount)
            val response = paymentClient.processPayment(request)
            // 결제 실패 시
            if (response.status != "SUCCESS") {
                log.error("[Payment] 실패: orderId={}, status={}", orderId, response.status)
                throw RuntimeException("결제 실패 (상태: ${response.status})")
            }
            log.info("[Payment] 성공: orderId={}, paymentId={}", orderId, response.paymentId)
            return response.paymentId
        } catch (e: Exception) {
            log.error("[Payment] API 호출 오류: orderId={}", orderId, e)
            throw RuntimeException("결제 시스템 연동 오류", e)
        }
    }
}