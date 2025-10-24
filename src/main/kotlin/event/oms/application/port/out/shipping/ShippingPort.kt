package event.oms.application.port.out.shipping

import event.oms.domain.model.order.ReceiverInfo

interface ShippingPort {
    /**
     * 배송 요청
     */
    fun requestShipping(orderId: Long, receiverInfo: ReceiverInfo): String
}