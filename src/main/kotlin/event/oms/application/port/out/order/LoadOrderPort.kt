package event.oms.application.port.out.order

import event.oms.domain.model.order.Order

interface LoadOrderPort {

    fun findOrderById(orderId: Long): Order?

    /**
     * 회원주문 목록 조회
     */
    fun findAllByMemberId(memberId: Long): List<Order>
}