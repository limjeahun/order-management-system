package event.oms.application.service.payment

import event.oms.application.port.`in`.payment.ConfirmPaymentCommand
import event.oms.application.port.`in`.payment.ConfirmPaymentUseCase
import event.oms.application.port.out.order.LoadOrderPort
import event.oms.application.port.out.order.SaveOrderPort
import event.oms.application.port.out.payment.PaymentPersistencePort
import event.oms.application.port.out.payment.TossPaymentPort
import event.oms.common.extensions.getLogger
import event.oms.domain.model.payment.PaymentStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ConfirmPaymentService(
    private val loadOrderPort         : LoadOrderPort,
    private val saveOrderPort         : SaveOrderPort,
    private val tossPaymentPort       : TossPaymentPort,
    private val paymentPersistencePort: PaymentPersistencePort,
): ConfirmPaymentUseCase {
    private val log = getLogger()

    @Transactional
    override fun confirmPayment(command: ConfirmPaymentCommand) {
        log.info("결제 승인 처리 시작: orderId={}, paymentKey={}", command.orderId, command.paymentKey)

        // 1. 결제 정보 체크
        val payment = paymentPersistencePort.findByPaymentKey(command.paymentKey)
            ?: throw NoSuchElementException("결제 정보를 찾을 수 없음: ${command.paymentKey}")

        if (payment.status != PaymentStatus.REQUESTED) {
            log.warn("이미 처리되었거나 잘못된 상태의 결제 승인 시도: paymentKey={}, status={}", command.paymentKey, payment.status)
            // Decide how to handle this (e.g., ignore, throw specific exception)
            return // Or throw IllegalStateException("이미 처리된 결제입니다.")
        }
        // 결제 금액 체크
        if (payment.orderId.toString() != command.orderId || payment.amount.compareTo(command.amount) != 0) {
            log.error("결제 정보 불일치: DB vs Toss Callback. Key={}, DB OrderId={}, Callback OrderId={}, DB Amount={}, Callback Amount={}",
                command.paymentKey, payment.orderId, command.orderId, payment.amount, command.amount)
            // TODO: Handle mismatch - potentially mark payment as FAILED
            payment.status = PaymentStatus.FAILED
            paymentPersistencePort.save(payment)
            throw IllegalArgumentException("결제 정보가 일치하지 않습니다.")
        }

        // 2. Toss Approval API 요청
        val approvalResponse = tossPaymentPort.confirmTossPayment(command.paymentKey, command.orderId, command.amount)
        
        // 3. 실패 시
        if (approvalResponse.status != "DONE") {
            payment.status = PaymentStatus.FAILED
            payment.approvedAt = LocalDateTime.now()
            paymentPersistencePort.save(payment)
            log.error("Toss 결제 승인 실패: paymentKey={}, status={}", command.paymentKey, approvalResponse.status)
        }
        
        // 3. 성공 시
        if (approvalResponse.status == "DONE") {
            // --- Payment Success ---
            payment.status = PaymentStatus.COMPLETED
            try {
                // Parse approvedAt string to LocalDateTime (adjust format if needed)
                payment.approvedAt = LocalDateTime.parse(approvalResponse.approvedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            } catch (e: Exception) {
                log.warn("Toss approvedAt 날짜 파싱 실패: {}, setting current time.", approvalResponse.approvedAt, e)
                payment.approvedAt = LocalDateTime.now() // Fallback
            }
            paymentPersistencePort.save(payment)
            log.info("결제 상태 COMPLETED로 변경: paymentKey={}", command.paymentKey)

            // Update Order status
            val order = loadOrderPort.findOrderById(payment.orderId)!! // Should exist
            order.markAsPaid()
            val paidOrder = saveOrderPort.save(order)
            log.info("주문 상태 PAID로 변경: orderId={}", paidOrder.id)

            // Trigger Shipping Request (Now that payment is confirmed)
            /*
            try {
                val trackingNumber = shippingPort.requestShipping(paidOrder.id!!, paidOrder.receiverInfo)
                // Create and save Shipping record
                val shipping = Shipping(
                    orderId = paidOrder.id!!,
                    trackingNumber = trackingNumber,
                    status = ShippingStatus.SHIPPED,
                    recipientName = paidOrder.receiverInfo.name,
                    recipientPhone = paidOrder.receiverInfo.phone,
                    address = paidOrder.receiverInfo.address,
                    shippedAt = LocalDateTime.now()
                )
                shippingPersistencePort.save(shipping)
                // Update Order status to SHIPPED
                paidOrder.markAsShipped()
                saveOrderPort.save(paidOrder)
                log.info("주문 상태 SHIPPED로 변경 및 배송 정보 저장: orderId={}", paidOrder.id)
            } catch (e: Exception) {
                log.error("배송 요청/저장 실패 (결제는 성공): orderId={}", paidOrder.id, e)
                // Save Shipping as FAILED, Order remains PAID
                val failedShipping = Shipping(
                    orderId = paidOrder.id!!, status = ShippingStatus.FAILED,
                    recipientName = paidOrder.receiverInfo.name, recipientPhone = paidOrder.receiverInfo.phone, address = paidOrder.receiverInfo.address
                )
                shippingPersistencePort.save(failedShipping)
                // TODO: Notify admin or schedule retry
            }
            */

        }
    }

}