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
    val version: Long,
) {

    /**
     * 제품 정보(이름, 가격, 재고)를 수정
     */
    fun updateDetails(changeName: String, changePrice: BigDecimal, changeStock: Int) {
        // 가격 유효성 검증
        if (changePrice <= BigDecimal.ZERO) throw IllegalArgumentException("상품 가격은 0보다 커야 합니다.")
        // 재고 유효성 검증
        if (changeStock < 0) throw IllegalArgumentException("재고를 입력해 주세요.")
        // 제품 정보 수정
        this.name  = changeName
        this.price = changePrice
        this.stock = changeStock
    }

    /**
     * 재고를 차감
     */
    fun decreaseStock(quantity: Int) {
        // 수량 확인
        if (quantity <= 0) {
            throw IllegalArgumentException("차감할 수량은 0보다 커야 합니다.")
        }

        val remainingStock = this.stock - quantity
        // 재고 확인
        if (remainingStock < 0) {
            throw IllegalArgumentException("재고가 부족합니다. (ID: ${this.id}, 현재: ${this.stock}, 요청: ${quantity})")
        }
        this.stock = remainingStock
    }

}