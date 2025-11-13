package event.oms.adapter.out.persistence.order.repository

import event.oms.adapter.out.persistence.order.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderJpaRepository : JpaRepository<OrderEntity, Long> {
    /**
     * 회원주문 목록 조회
     */
    fun findAllByMemberId(memberId: Long): List<OrderEntity>

    /**
     * 회원주문 상세 조회
     */
    fun findByTraceId(traceId: String): OrderEntity?

    /**
     * 주문, 회원 ID로 주문 조회
     */
    fun findByIdAndMemberId (orderId: Long, memberId: Long): OrderEntity?
}