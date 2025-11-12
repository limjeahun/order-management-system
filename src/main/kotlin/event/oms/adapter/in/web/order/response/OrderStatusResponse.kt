package event.oms.adapter.`in`.web.order.response

import event.oms.application.port.`in`.order.OrderSummaryResult
import event.oms.domain.model.order.OrderTraceStatus

data class OrderStatusResponse(
    val status : OrderTraceStatus,
    val orderId: Long,
    val traceId: String,
) {
    companion object {
        fun from(result: OrderSummaryResult): OrderStatusResponse {
            return OrderStatusResponse(
                result.status,
                result.orderId?: 0L,
                result.traceId,
            )
        }

    }
}
