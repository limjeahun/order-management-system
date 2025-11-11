package event.oms.adapter.out.redis

import event.oms.application.port.out.waitlist.WaitListPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisWaitListAdapter(
    private val redisTemplate: RedisTemplate<String, Any>,
): WaitListPort {
    @Value("\${promotion.max-active-users:1000}")
    private val MAX_ACTIVE_USERS: Long = 1000L

    private val ACTIVE_USER_COUNT_KEY = "promotion:active:count"
    private val ACTIVE_USER_SET_KEY = "promotion:active:users" // Set으로 활성 유저 관리
    private val WAITING_QUEUE_KEY = "promotion:wait:queue" // ZSET
    private val ACTIVE_USER_TTL_SECONDS: Long = 300 // 5분

    override fun getActiveUserCount(): Long {
        // Set의 크기
        return redisTemplate.opsForSet().size(ACTIVE_USER_SET_KEY) ?: 0L
    }

    override fun incrementActiveUserCount(userId: String): Long {
        redisTemplate.opsForSet().add(ACTIVE_USER_SET_KEY, userId)
        return getActiveUserCount()
    }

    override fun addToWaitingQueue(userId: String): Long {
        redisTemplate.opsForZSet().add(WAITING_QUEUE_KEY, userId, System.currentTimeMillis().toDouble())
        return getWaitingRank(userId) ?: 0L
    }

    override fun getWaitingRank(userId: String): Long? {
        return redisTemplate.opsForZSet().rank(WAITING_QUEUE_KEY, userId)
    }

    override fun isUserActive(userId: String): Boolean {
        return redisTemplate.opsForSet().isMember(ACTIVE_USER_SET_KEY, userId) ?: false
    }
}