package event.oms.application.port.out.trace

interface LoadOrderTracePort {
    /**
     * traceId로 주문 추적 상태를 조회.
     */
    fun findByTraceId(traceId: String): OrderTraceStatus?
}