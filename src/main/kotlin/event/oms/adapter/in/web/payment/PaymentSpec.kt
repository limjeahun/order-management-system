package event.oms.adapter.`in`.web.payment

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import java.math.BigDecimal


@Tag(name = "Payment Callback API", description = "결제 콜백 처리 API")
interface PaymentSpec {
    @Operation(summary = "Toss Payments 결제 성공 콜백", description = "Toss Payments 에서 사용자가 결제를 성공적으로 완료한 후 호출되는 Redirect URL")
    fun handleTossSuccessCallback(
        @Parameter(description = "Toss에서 발급한 결제 키")
        paymentKey: String,
        @Parameter(description = "주문 ID")
        orderId   : String,
        @Parameter(description = "결제 금액")
        amount    : BigDecimal,
    ): ResponseEntity<String>

    @Operation(summary = "Toss Payments 결제 실패 콜백", description = "Toss Payments 에서 사용자가 결제를 실패/취소한 후 호출되는 Redirect URL")
    fun handleTossFailCallback(
        @Parameter(description = "Toss 에러 코드")
        code   : String,
        @Parameter(description = "에러 메시지")
        message: String,
        @Parameter(description = "주문 ID")
        orderId: String,
    ): ResponseEntity<String>

}