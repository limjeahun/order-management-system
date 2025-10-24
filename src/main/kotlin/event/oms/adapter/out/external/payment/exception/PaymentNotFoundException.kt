package event.oms.adapter.out.external.payment.exception

/**
 * API 호출 시 리소스를 찾을 수 없을 때 (e.g., HTTP 404) 발생하는 예외
 */
class PaymentNotFoundException(message: String) : RuntimeException(message) {

}