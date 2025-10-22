package event.oms.adapter.`in`.web.product.response

import event.oms.domain.model.product.Product
import java.math.BigDecimal

data class ProductResponse(
    val productId: Long,
    var name     : String,
    var price    : BigDecimal,
    var stock    : Int,
) {
    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                productId = product.id,
                name      = product.name,
                price     = product.price,
                stock     = product.stock,
            )
        }
    }
}
