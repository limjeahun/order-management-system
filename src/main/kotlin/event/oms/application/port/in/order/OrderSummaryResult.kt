package event.oms.application.port.`in`.order

data class OrderSummaryResult(
    val status : String, // "PROCESSING", "COMPLETED", "FAILED"
    val orderId: Long?, // 완료 시에만 실제 orderId 전달
    val traceId: String,
)
