package event.oms.adapter.`in`.web.payment

import event.oms.application.port.`in`.payment.ConfirmPaymentCommand
import event.oms.application.port.`in`.payment.ConfirmPaymentUseCase
import event.oms.common.extensions.getLogger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/payments/toss")
class TossPaymentController(
    private val confirmPaymentUseCase: ConfirmPaymentUseCase,
): PaymentSpec {
    private val log = getLogger()

    @GetMapping("/success")
    override fun handleTossSuccessCallback(
        @RequestParam paymentKey: String,
        @RequestParam orderId   : String,
        @RequestParam amount    : BigDecimal,
    ): ResponseEntity<String> {
        log.info("Toss Success Callback 수신: orderId={}, paymentKey={}", orderId, paymentKey)
        val command = ConfirmPaymentCommand(paymentKey, orderId, amount)
        confirmPaymentUseCase.confirmPayment(command)
        // 성공 시 사용자에게 보여줄 페이지로 리다이렉트 또는 성공 메시지 반환
        // 예: return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/payment/success-page")).build()
        return ResponseEntity.ok("결제가 성공적으로 완료되었습니다. OrderId: $orderId")
    }

    @GetMapping("/fail")
    override fun handleTossFailCallback(
        @RequestParam code   : String,
        @RequestParam message: String,
        @RequestParam orderId: String,
    ): ResponseEntity<String> { // Redirect or show failure message
        log.warn("Toss Fail Callback 수신: orderId={}, code={}, message={}", orderId, code, message)
        // 사용자에게 보여줄 실패 페이지로 리다이렉트 또는 실패 메시지 반환
        // 예: return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/payment/fail-page?message=" + message)).build()
        return ResponseEntity.badRequest().body("결제에 실패했습니다. Reason: $message (OrderId: $orderId)")
    }

}