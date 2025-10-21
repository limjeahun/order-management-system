package event.oms.application.port.`in`.product

import event.oms.domain.model.product.Product

/**
 * 제품 수정 유스케이스 Inbound Port
 */
interface UpdateProductUseCase {
    fun updateProduct(command: UpdateProductCommand): Product
}