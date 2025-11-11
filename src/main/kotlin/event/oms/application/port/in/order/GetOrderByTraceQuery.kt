package event.oms.application.port.`in`.order

interface GetOrderByTraceQuery {
    fun getOrderSummaryByTrace(traceId: String): OrderSummaryResult
}