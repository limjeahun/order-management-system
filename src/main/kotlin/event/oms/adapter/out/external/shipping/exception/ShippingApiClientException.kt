package event.oms.adapter.out.external.shipping.exception

/**
 * API 호출 시 클라이언트 측 오류 (잘못된 요청 등, 404 제외)가 발생했을 때 (e.g., HTTP 400, 401, 403) 발생하는 예외.
 */
class ShippingApiClientException(message: String) : RuntimeException(message) {

}