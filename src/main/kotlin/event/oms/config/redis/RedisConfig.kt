package event.oms.config.redis

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableCaching
class RedisConfig {

    /**
     * 캐시 (Member 객체 등)를 위한 RedisTemplate
     * Key: String, Value: JSON (Generic)
     */
    @Bean
    fun cacheRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer() // 객체를 JSON으로 직렬화
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = GenericJackson2JsonRedisSerializer()
        return template
    }

    /**
     * JWT 토큰 관리를 위한 RedisTemplate
     * Key: String (Token), Value: String (Username)
     */
    @Bean(name = ["tokenRedisTemplate"])
    fun tokenRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        return template
    }

}