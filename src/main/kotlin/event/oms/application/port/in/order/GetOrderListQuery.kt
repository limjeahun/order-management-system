package event.oms.application.port.`in`.order

import event.oms.domain.model.order.Order

interface GetOrderListQuery {
    /**
     * 회원 주문 목록 정보 조회
     */
    fun getAllOrdersByMember(memberId: Long): List<Pair<Order, Map<Long, String>>>
}