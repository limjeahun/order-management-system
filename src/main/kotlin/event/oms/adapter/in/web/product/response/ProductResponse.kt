package event.oms.adapter.`in`.web.product.response

import event.oms.domain.model.product.Product
import java.math.BigDecimal

data class ProductResponse(
    val productId: Long
) {
    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                productId = product.id,
            )
        }
    }
}
