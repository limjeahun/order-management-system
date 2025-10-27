package event.oms.domain.model.order

import java.time.LocalDateTime

class Order(
    val id          : Long? = null,
    val memberId    : Long,
    val orderItems  : List<OrderItem>,
    var status      : OrderStatus,
    val orderDate   : LocalDateTime,
    val receiverInfo: ReceiverInfo,
) {

    fun confirmation() {
        if (this.status != OrderStatus.PENDING) {
            throw IllegalStateException("결제를 진행할 수 없는 주문 상태입니다: ${this.status}")
        }
    }


}