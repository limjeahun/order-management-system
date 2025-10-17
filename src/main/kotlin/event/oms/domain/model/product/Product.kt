package event.oms.domain.model.product

import java.math.BigDecimal

class Product(
    val id   : Long,
    var name : String,
    var price: BigDecimal,
    var stock: Int,
) {
}