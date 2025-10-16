package event.oms.adapter.out.persistence.order.entity

import event.oms.domain.model.order.OrderStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class OrderEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val memberId: Long,
    @Enumerated(EnumType.STRING)
    var status: OrderStatus,
    val orderDate: LocalDateTime
) {
}