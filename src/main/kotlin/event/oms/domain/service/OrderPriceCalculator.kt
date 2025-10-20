package event.oms.domain.service

import event.oms.domain.model.order.OrderItem
import event.oms.domain.model.product.Product
import org.springframework.stereotype.Component

/**
 * 가격 계산, 재고 확인
 */
@Component
class OrderPriceCalculator {
    /**
     * 주문 제품의 재고 검증 및 가격 확인
     * 가격과 재고가 검증된 OrderItem 리스트를 생성.
     */
    fun calculateOrderItems(
        itemRequests: List<ItemRequest>,
        products    : List<Product>,
    ): List<OrderItem> {
        return itemRequests.map { request ->
            val product = products.find { it.id == request.productId }
                ?: throw NoSuchElementException("상품 정보를 찾을 수 없습니다: ${request.productId}")
            // 재고 확인
            if (product.stock < request.quantity) {
                throw IllegalArgumentException(
                    "재고가 부족합니다: ${product.name} (요청: ${request.quantity}, 현재 재고: ${product.stock})"
                )
            }
            // 검증이 완료된 OrderItem 생성
            OrderItem(
                productId = request.productId,
                price     = product.price,
                quantity  = request.quantity
            )
        }
    }

    /**
     * 가격, 재고 검증 데이터 객체
     */
    data class ItemRequest(
        val productId: Long,
        val quantity : Int,
    )

}