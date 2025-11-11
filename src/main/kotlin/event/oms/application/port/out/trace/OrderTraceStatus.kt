package event.oms.application.port.out.trace

/**
 * 주문 추적 상태를 나타내는 DTO
 */
data class OrderTraceStatus(
    val status: String, // PROCESSING, COMPLETED, FAILED
    val orderId: Long? = null,
)
