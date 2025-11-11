package event.oms.config.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaConfig {
    @Value("\${promotion.oder-topic}")
    private lateinit var orderTopic: String

    @Value("\${promotion.dlq-topic}")
    private lateinit var dlqTopic: String

    /**
     * 애플리케이션 시작 시 'order-request-v1' 토픽을 자동으로 생성.
     */
    @Bean
    fun orderRequestTopic(): NewTopic {
        return TopicBuilder.name(orderTopic)
            .partitions(1)  // 로컬 테스트용 파티션 1개
            .replicas(1)    // 로컬 테스트용 리플리카 1개
            .build()
    }

    /**
     *
     */
    @Bean
    fun orderRequestDlqTopic(): NewTopic {
        return TopicBuilder.name(dlqTopic)
            .partitions(1)
            .replicas(1)
            .build()
    }


}