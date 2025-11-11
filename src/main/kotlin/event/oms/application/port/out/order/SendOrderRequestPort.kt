package event.oms.application.port.out.order

import event.oms.application.port.`in`.order.OrderCommand

interface SendOrderRequestPort {
    /**
     * 주문 요청 메시지를 기본 토픽으로 전송
     */
    fun send(command: OrderCommand)

    /**
     * 주문 요청 메시지를 Dead-Letter Queue (DLQ) 토픽으로 전송
     */
    fun sendToDlq(command: OrderCommand)
}