package event.oms.application.port.`in`.payment

interface RequestPaymentUseCase {
    /**
     * 결제 요청
     */
    fun requestPayment(orderId: Long): PaymentRequestResult
}