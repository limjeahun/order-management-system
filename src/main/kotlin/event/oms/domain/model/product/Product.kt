package event.oms.domain.model.product

import java.math.BigDecimal

/**
 * 제품 (Model)
 */
class Product(
    val id   : Long,
    var name : String,
    var price: BigDecimal,
    var stock: Int,
) {

    /**
     * 제품 정보(이름, 가격, 재고)를 수정
     */
    fun updateDetails(product: Product) {
        // 가격 유효성 검증
        if (product.price <= BigDecimal.ZERO) throw IllegalArgumentException("상품 가격은 0보다 커야 합니다.")
        // 재고 유효성 검증
        if (product.stock < 0) throw IllegalArgumentException("재고를 입력해 주세요.")
        // 제품 정보 수정
        this.name  = product.name
        this.price = product.price
        this.stock = product.stock
    }
}