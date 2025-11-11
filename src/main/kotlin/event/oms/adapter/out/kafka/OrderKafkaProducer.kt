package event.oms.adapter.out.kafka

import event.oms.application.port.`in`.order.OrderCommand
import event.oms.application.port.out.order.SendOrderRequestPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OrderKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, OrderCommand>,

    @Value("\${promotion.order-topic}")
    private val topicName: String,

    @Value("\${promotion.dlq-topic}")
    private val dlqTopicName: String,
): SendOrderRequestPort {
    /**
     * 주문 요청 메시지를 기본 토픽으로 전송
     */
    override fun send(command: OrderCommand) {
        // 동일 사용자의 주문 순서를 보장하기 위해 memberId를 메시지 키로 사용
        kafkaTemplate.send(
            topicName,
            command.memberId.toString(),
            command
        )
    }

    /**
     * 주문 요청 메시지를 Dead-Letter Queue (DLQ) 토픽으로 전송
     */
    override fun sendToDlq(command: OrderCommand) {
        // 실패한 메시지는 DLQ로 전송
        kafkaTemplate.send(
            dlqTopicName,
            command.memberId.toString(),
            command
        )
    }


}