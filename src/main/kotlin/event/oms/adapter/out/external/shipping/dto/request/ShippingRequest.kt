package event.oms.adapter.out.external.shipping.dto.request

data class ShippingRequest(
    val orderId       : Long,
    val recipientName : String,
    val recipientPhone: String,
    val address       : String,
)
