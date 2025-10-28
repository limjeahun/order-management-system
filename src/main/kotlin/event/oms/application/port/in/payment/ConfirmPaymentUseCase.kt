package event.oms.application.port.`in`.payment

interface ConfirmPaymentUseCase {
    fun confirmPayment(command: ConfirmPaymentCommand)
}