package event.oms.application.port.out.trace

import event.oms.domain.model.order.OrderTraceStatus

/**
 * 주문 추적 상태를 나타내는 DTO
 */
data class OrderTraceResult(
    val status: OrderTraceStatus, // PROCESSING, COMPLETED, FAILED
    val orderId: Long? = null,
)
