package event.oms.adapter.out.external.shipping.exception

/**
 * API 서버 자체에 문제가 발생했을 때 (e.g., HTTP 500, 503) 발생하는 예외
 */
class ShippingServerException(message: String) : RuntimeException(message) {

}