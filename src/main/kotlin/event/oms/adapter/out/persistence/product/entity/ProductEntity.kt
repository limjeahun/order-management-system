package event.oms.adapter.out.persistence.product.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.math.BigDecimal

@Entity
@Table(name = "product")
class ProductEntity(
    @Id
    val id: Long,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var price: BigDecimal,
    val stock: Int,
    @Version
    val version: Long = 0L
) {

}