package event.oms.adapter.out.redis

import event.oms.application.port.out.member.ManageTokenPort
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisTokenAdapter(
    @Qualifier("tokenRedisTemplate") // String, String 타입의 RedisTemplate
    private val redisTemplate: RedisTemplate<String, String>
): ManageTokenPort {
    companion object {
        // Key: username, Value: RefreshToken
        const val REFRESH_TOKEN_PREFIX = "jwt:rt:"
    }

    override fun saveRefreshToken(username: String, refreshToken: String, expirationMs: Long) {
        val key = REFRESH_TOKEN_PREFIX + username
        redisTemplate.opsForValue().set(key, refreshToken, expirationMs, TimeUnit.MILLISECONDS)
    }

    override fun findRefreshTokenByUsername(username: String): String? {
        val key = REFRESH_TOKEN_PREFIX + username
        return redisTemplate.opsForValue().get(key)
    }

    override fun deleteRefreshTokenByUsername(username: String) {
        val key = REFRESH_TOKEN_PREFIX + username
        redisTemplate.delete(key)
    }


}