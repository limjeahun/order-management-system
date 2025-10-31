package event.oms.adapter.out.persistence.order.entity

import event.oms.adapter.out.persistence.support.AbstractJpaEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "order_item")
class OrderItemEntity(
    val orderId  : Long, // 직접적인 FK 참조
    val productId: Long,
    val price    : BigDecimal,
    val quantity : Int,
): AbstractJpaEntity() {

}


