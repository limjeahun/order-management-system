package event.oms.domain.model.order

import java.math.BigDecimal
import java.time.LocalDateTime

class Order(
    val id          : Long? = null,
    val traceId     : String,
    val memberId    : Long,
    val orderItems  : List<OrderItem>,
    var status      : OrderStatus,
    val orderDate   : LocalDateTime,
    val receiverInfo: ReceiverInfo,
) {
    /**
     * 총 상품 금액
     */
    fun getTotalPrice(): BigDecimal {
        return orderItems.sumOf { it.price * it.quantity.toBigDecimal() }
    }

    /**
     * 결제 상태로 변경
     */
    fun markAsPaid() {
        if (this.status != OrderStatus.PENDING) {
            throw IllegalStateException("결제를 진행할 수 없는 주문 상태입니다: ${this.status}")
        }
        this.status = OrderStatus.PAID
    }

    /**
     * 간단한 주문 내용 가져오기
     */
    fun getOrderName(): String {
        return orderItems.firstOrNull()?.let {
            if (orderItems.size > 1) "${it.productId} 외 ${orderItems.size - 1}건" else "${it.productId}"
        } ?: "주문 상품 없음"
    }


}