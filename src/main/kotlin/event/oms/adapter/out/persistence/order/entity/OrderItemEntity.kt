package event.oms.adapter.out.persistence.order.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "order_item")
class OrderItemEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val orderId: Long, // 직접적인 FK 참조
    val productId: Long,
    val price: BigDecimal,
    val quantity: Int,
) {

}


