package event.oms.application.service.payment

import event.oms.application.port.`in`.payment.PaymentRequestResult
import event.oms.application.port.`in`.payment.RequestPaymentUseCase
import event.oms.application.port.out.order.LoadOrderPort
import event.oms.application.port.out.payment.PaymentPersistencePort
import event.oms.application.port.out.payment.TossPaymentPort
import event.oms.common.extensions.getLogger
import event.oms.domain.model.order.OrderStatus
import event.oms.domain.model.payment.Payment
import event.oms.domain.model.payment.PaymentStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class RequestPaymentService(
    private val loadOrderPort         : LoadOrderPort,
    private val tossPaymentPort       : TossPaymentPort,
    private val paymentPersistencePort: PaymentPersistencePort,
    @Value("\${server.port}") private val serverPort: Int,
    @Value("\${server.servlet.context-path:/}") private val contextPath: String,
): RequestPaymentUseCase {
    private val log = getLogger()

    @Transactional
    override fun requestPayment(orderId: Long): PaymentRequestResult {
        log.info("결제 요청 시작 (Service): orderId={}", orderId)
        val order = loadOrderPort.findOrderById(orderId)
            ?: throw NoSuchElementException("결제 요청할 주문 없음: $orderId")

        if (order.status != OrderStatus.PENDING) {
            throw IllegalStateException("이미 처리되었거나 결제할 수 없는 주문입니다.")
        }

        val amount = order.orderItems.sumOf { it.price.multiply(BigDecimal.valueOf(it.quantity.toLong())) }
        val orderName = order.orderItems.firstOrNull()?.let {
            if (order.orderItems.size > 1) "${it.productId} 외 ${order.orderItems.size - 1}건" else "${it.productId}"
        } ?: "주문 상품 없음"

        val baseUrl = "http://localhost:${serverPort}${contextPath}".removeSuffix("/")
        val successUrl = "$baseUrl/api/v1/payments/toss/success"
        val failUrl = "$baseUrl/api/v1/payments/toss/fail"

        // Toss Request API
        val tossResponse = tossPaymentPort.requestTossPayment(orderId, orderName, amount, successUrl, failUrl)

        // Payment 저장
        val payment = Payment(
            orderId     = orderId,
            paymentKey  = tossResponse.paymentKey,
            amount      = amount,
            status      = PaymentStatus.REQUESTED, // Initial status
            requestedAt = LocalDateTime.now(),
            approvedAt  = LocalDateTime.now(),
        )

        paymentPersistencePort.save(payment)
        log.info("결제 정보 저장 (REQUESTED): orderId={}, paymentKey={}", orderId, tossResponse.paymentKey)

        // Return result to Controller -> Frontend
        return PaymentRequestResult(
            orderId     = orderId,
            paymentKey  = tossResponse.paymentKey,
            redirectUrl = tossResponse.checkout?.url ?: throw IllegalStateException("Toss 결제 페이지 URL 없음")
        )
    }


}