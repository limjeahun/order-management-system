package event.oms.adapter.out.external.shipping

import event.oms.adapter.out.external.shipping.dto.request.ShippingRequest
import event.oms.application.port.out.shipping.ShippingPort
import event.oms.domain.model.order.ReceiverInfo
import org.slf4j.LoggerFactory

class ShippingAdapter(
    private val shippingClient: ShippingClient,
): ShippingPort {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 배송 API 요청
     */
    override fun requestShipping(orderId: Long, receiverInfo: ReceiverInfo): String {
        log.info("[Shipping] 요청 시작: orderId={}", orderId)
        try {
            val request = ShippingRequest(
                orderId        = orderId,
                recipientName  = receiverInfo.name,
                recipientPhone = receiverInfo.phone,
                address        = receiverInfo.address
            )
            val response = shippingClient.requestShipment(request)
            // 요청 실패 시
            if (response.status != "REQUESTED") {
                log.error("[Shipping] 실패: orderId={}, status={}", orderId, response.status)
                throw RuntimeException("배송 요청 실패 (상태: ${response.status})")
            }
            log.info("[Shipping] 성공: orderId={}, trackingNumber={}", orderId, response.trackingNumber)
            return response.trackingNumber
        } catch (e: Exception) {
            log.error("[Shipping] API 호출 오류: orderId={}", orderId, e)
            throw RuntimeException("배송 시스템 연동 오류", e)
        }
    }

}