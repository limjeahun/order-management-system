package event.oms.application.port.`in`.product

import event.oms.domain.model.product.Product

/**
 * '상품 등록' 유스케이스 Inbound Port
 */
interface AddProductUseCase {
    fun addProduct(command: AddProductCommand): Product
}