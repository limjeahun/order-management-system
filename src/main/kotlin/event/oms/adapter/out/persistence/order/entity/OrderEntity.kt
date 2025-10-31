package event.oms.adapter.out.persistence.order.entity

import event.oms.adapter.out.persistence.order.embeddable.ReceiverInfoEmbeddable
import event.oms.domain.model.order.OrderStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class OrderEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id          : Long? = null,
    val memberId    : Long,
    @Enumerated(EnumType.STRING)
    var status      : OrderStatus,
    val orderDate   : LocalDateTime,
    @Embedded
    val receiverInfo: ReceiverInfoEmbeddable,
) {

}