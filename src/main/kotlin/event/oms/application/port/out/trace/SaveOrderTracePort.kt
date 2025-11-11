package event.oms.application.port.out.trace

interface SaveOrderTracePort {
    /**
     * 주문 추적 상태를 저장.
     */
    fun save(traceId: String, status: OrderTraceStatus)
}