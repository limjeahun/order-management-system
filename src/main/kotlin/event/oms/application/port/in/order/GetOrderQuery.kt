package event.oms.application.port.`in`.order

import event.oms.domain.model.order.Order

interface GetOrderQuery {
    /**
     * 주문 상세 정보 조회
     */
    fun getOrder(orderId: Long): Pair<Order, Map<Long, String>>
}