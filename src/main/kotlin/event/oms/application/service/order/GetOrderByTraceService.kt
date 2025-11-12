package event.oms.application.service.order

import event.oms.application.port.`in`.order.GetOrderByTraceQuery
import event.oms.application.port.`in`.order.OrderSummaryResult
import event.oms.application.port.out.order.LoadOrderPort
import event.oms.application.port.out.trace.LoadOrderTracePort
import event.oms.application.port.out.trace.OrderTraceStatus
import event.oms.application.port.out.trace.SaveOrderTracePort
import org.springframework.stereotype.Service

@Service
class GetOrderByTraceService(
    private val loadOrderPort     : LoadOrderPort,
    private val loadOrderTracePort: LoadOrderTracePort,
    private val saveOrderTracePort: SaveOrderTracePort,
): GetOrderByTraceQuery {
    override fun getOrderSummaryByTrace(traceId: String): OrderSummaryResult {
        try {
            // 1. Redis(캐시)에서 상태 조회
            val cachedStatus = loadOrderTracePort.findByTraceId(traceId)
            if (cachedStatus != null) {
                // 1-1. 캐시 히트
                return OrderSummaryResult(
                    status  = cachedStatus.status,
                    orderId = cachedStatus.orderId,
                    traceId = traceId
                )
            }
            // 2. 캐시 미스 (DB로 Fallback)
            val order = loadOrderPort.findByTraceId(traceId)
                ?: return OrderSummaryResult(
                    status  = "PROCESSING",
                    orderId = null,
                    traceId = traceId
                )
            // 3. DB 조회 결과를 다시 캐시에 저장 (Write-back)
            val statusToCache = OrderTraceStatus(status = "COMPLETED", orderId = order.id)
            saveOrderTracePort.save(traceId, statusToCache)
            // 4. 주문 존재 시
            return OrderSummaryResult(
                status  = "COMPLETED",
                orderId = order.id,
                traceId = traceId,
            )
        }catch (e: Exception) {
            return OrderSummaryResult(status = "ERROR", orderId = null, traceId = traceId)
        }
    }

}