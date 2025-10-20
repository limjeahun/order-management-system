package event.oms.application.port.out.product

import event.oms.domain.model.product.Product

interface LoadProductPort {
    fun findAllByIds(productIds: List<Long>): List<Product>
}