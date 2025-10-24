package event.oms.adapter.out.external.shipping.exception

/**
 * API 호출 시 리소스를 찾을 수 없을 때 (e.g., HTTP 404) 발생하는 예외
 */
class ShippingNotFoundException(message: String) : RuntimeException(message) {

}