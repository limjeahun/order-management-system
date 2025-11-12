package event.oms.adapter.out.redis

import event.oms.application.port.out.trace.LoadOrderTracePort
import event.oms.application.port.out.trace.OrderTraceResult
import event.oms.application.port.out.trace.SaveOrderTracePort
import event.oms.domain.model.order.OrderTraceStatus
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class OrderTraceRedisAdapter(
    private val redisTemplate: RedisTemplate<String, Any>
): SaveOrderTracePort, LoadOrderTracePort {
    companion object {
        private const val TRACE_KEY_PREFIX = "order:trace:"
        private const val TRACE_KEY_TTL_MINUTES: Long = 10
    }

    override fun save(traceId: String, memberId: Long, status: OrderTraceResult) {
        val key = "$TRACE_KEY_PREFIX$traceId:$memberId"
        val statusMap = mapOf(
            "status" to status.status.name,
            "orderId" to status.orderId
        ).filterValues { it != null } // orderId가 null이면 맵에서 제외

        redisTemplate.opsForValue().set(
            key,
            statusMap,
            TRACE_KEY_TTL_MINUTES,
            TimeUnit.MINUTES
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun findByTraceId(traceId: String, memberId: Long): OrderTraceResult? {
        val key = "$TRACE_KEY_PREFIX$traceId:$memberId"
        val cachedStatus = redisTemplate.opsForValue().get(key) as? Map<String, Any>
            ?: return null // 캐시에 없으면 null 반환

        // 맵을 DTO로 변환
        return OrderTraceResult(
            status =  OrderTraceStatus.valueOf(cachedStatus["status"] as String),
            orderId = (cachedStatus["orderId"] as? Number)?.toLong()
        )
    }
}