package event.oms.adapter.out.persistence.product.mapper

import event.oms.adapter.out.persistence.product.entity.ProductEntity
import event.oms.domain.model.product.Product
import org.springframework.stereotype.Component

@Component
class ProductMapper {
    fun toEntity(product: Product): ProductEntity {
        return ProductEntity(
            id = product.id,
            name = product.name,
            price = product.price,
            stock = product.stock,
        )
    }

    fun toDomain(productEntity: ProductEntity): Product {
        return Product(
            id = productEntity.id,
            name = productEntity.name,
            price = productEntity.price,
            stock = productEntity.stock,
        )
    }
}