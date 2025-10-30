package event.oms.adapter.out.persistence.order.repository

import event.oms.adapter.out.persistence.order.entity.OrderItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderItemJpaRepository : JpaRepository<OrderItemEntity, Long> {

    fun findByOrderId(orderId: Long): List<OrderItemEntity>

    fun findByOrderIdIn(orderIds: List<Long>): List<OrderItemEntity>
}