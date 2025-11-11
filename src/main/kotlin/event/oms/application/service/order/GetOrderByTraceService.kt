package event.oms.application.service.order

import event.oms.application.port.`in`.order.GetOrderByTraceQuery
import event.oms.application.port.`in`.order.OrderSummaryResult
import event.oms.application.port.out.order.LoadOrderPort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class GetOrderByTraceService(
    private val loadOrderPort: LoadOrderPort,
    private val redisTemplate: RedisTemplate<String, Any>, // (RedisConfig에 정의된 cacheRedisTemplate을 주입받습니다)
): GetOrderByTraceQuery {
    override fun getOrderSummaryByTrace(traceId: String): OrderSummaryResult {
        val order = loadOrderPort.findByTraceId(traceId)
            ?: return OrderSummaryResult(
                status = "PROCESSING",
                orderId = null,
                traceId = traceId
            )
        // 주문 존재 시
        return OrderSummaryResult(
            status  = "COMPLETED",
            orderId = order.id,
            traceId = traceId,
        )
    }

}