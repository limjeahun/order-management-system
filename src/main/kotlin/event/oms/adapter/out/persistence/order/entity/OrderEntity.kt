package event.oms.adapter.out.persistence.order.entity

import event.oms.adapter.out.persistence.order.embeddable.ReceiverInfoEmbeddable
import event.oms.adapter.out.persistence.support.AbstractJpaEntity
import event.oms.domain.model.order.OrderStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class OrderEntity(
    val memberId    : Long,
    @Enumerated(EnumType.STRING)
    var status      : OrderStatus,
    val orderDate   : LocalDateTime,
    @Embedded
    val receiverInfo: ReceiverInfoEmbeddable,
): AbstractJpaEntity() {

}