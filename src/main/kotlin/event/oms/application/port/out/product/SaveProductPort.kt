package event.oms.application.port.out.product

import event.oms.domain.model.product.Product

/**
 * 상품 저장을 위한 Outbound Port
 */
interface SaveProductPort {
    fun save(product: Product): Product
}