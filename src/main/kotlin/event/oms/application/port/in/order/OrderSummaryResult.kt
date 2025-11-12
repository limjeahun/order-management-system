package event.oms.application.port.`in`.order

import event.oms.domain.model.order.OrderTraceStatus

data class OrderSummaryResult(
    val status : OrderTraceStatus, // "PROCESSING", "COMPLETED", "FAILED"
    val orderId: Long?, // 완료 시에만 실제 orderId 전달
    val traceId: String,
)
