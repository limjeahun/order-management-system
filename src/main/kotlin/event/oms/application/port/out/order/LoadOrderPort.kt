package event.oms.application.port.out.order

import event.oms.domain.model.order.Order

interface LoadOrderPort {
    /**
     * ID로 주문 조회
     */
    fun findOrderById(orderId: Long): Order

    /**
     * 회원 ID로 회원주문 목록 조회
     */
    fun findAllByMemberId(memberId: Long): List<Order>

    /**
     * traceId로 회원주문 목록 조회
     */
    fun findByTraceId(traceId: String): Order?
}