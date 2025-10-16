package event.oms.adapter.out.persistence.order.embeddable

import jakarta.persistence.Embeddable

@Embeddable
class ReceiverInfoEmbeddable(
    val receiverName   : String,
    val receiverPhone  : String,
    val receiverAddress: String,
) {
}