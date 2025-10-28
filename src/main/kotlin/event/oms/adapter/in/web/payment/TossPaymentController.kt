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
        try {
            val command = ConfirmPaymentCommand(paymentKey, orderId, amount)
            confirmPaymentUseCase.confirmPayment(command)

            // 성공 시 사용자에게 보여줄 페이지로 리다이렉트 또는 성공 메시지 반환
            // 예: return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/payment/success-page")).build()
            return ResponseEntity.ok("결제가 성공적으로 완료되었습니다. OrderId: $orderId")

        } catch (e: NoSuchElementException) {
            log.warn("Toss Success Callback 처리 실패 (데이터 없음): {}", e.message)
            // 사용자에게 보여줄 실패 페이지로 리다이렉트 또는 에러 메시지 반환
            return ResponseEntity.status(404).body("결제 정보를 찾을 수 없습니다: ${e.message}")
        } catch (e: IllegalArgumentException) {
            log.warn("Toss Success Callback 처리 실패 (정보 불일치): {}", e.message)
            return ResponseEntity.status(400).body("결제 정보가 일치하지 않습니다: ${e.message}")
        } catch (e: Exception) {
            log.error("Toss Success Callback 처리 중 오류 발생: orderId={}, paymentKey={}", orderId, paymentKey, e)
            // 사용자에게 보여줄 실패 페이지로 리다이렉트 또는 에러 메시지 반환
            return ResponseEntity.status(500).body("결제 처리 중 오류가 발생했습니다. 관리자에게 문의하세요.")
        }
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