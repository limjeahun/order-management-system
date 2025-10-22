package event.oms.adapter.out.persistence.order.repository

import event.oms.adapter.out.persistence.order.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderJpaRepository : JpaRepository<OrderEntity, Long> {

    fun findAllByMemberId(memberId: Long): List<OrderEntity>
}