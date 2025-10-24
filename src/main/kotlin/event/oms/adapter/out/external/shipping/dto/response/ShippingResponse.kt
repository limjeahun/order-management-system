package event.oms.adapter.out.external.shipping.dto.response

data class ShippingResponse(
    val trackingNumber: String,
    val status        : String,
)
