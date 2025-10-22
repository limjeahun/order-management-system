package event.oms.application.port.`in`.product

import event.oms.domain.model.product.Product

/**
 * 제품 목록 조회 유스케이스 Inbound Port
 */
interface GetProductListQuery {
    fun getAllProducts(): List<Product>
}