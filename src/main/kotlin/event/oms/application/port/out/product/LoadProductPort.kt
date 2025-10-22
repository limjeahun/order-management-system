package event.oms.application.port.out.product

import event.oms.domain.model.product.Product

/**
 * 제품 정보 조회 Port
 */
interface LoadProductPort {
    fun findAllByIds(productIds: List<Long>): List<Product>
    fun findById(productId: Long): Product?
    fun findAll(): List<Product>
}