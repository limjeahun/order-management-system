package event.oms.adapter.out.persistence.product.mapper

import event.oms.adapter.out.persistence.product.entity.ProductEntity
import event.oms.domain.model.product.Product

/**
 * 도메인 Product 객체를 JPA ProductEntity 객체 변환
 * @return ProductEntity 객체
 */
fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id      = id,
        name    = name,
        price   = price,
        stock   = stock,
        version = version,
    )
}

/**
 * JPA ProductEntity 객체를 도메인 Product 객체로 변환
 * @return Product 객체
 */
fun ProductEntity.toDomain(): Product {
    return Product(
        id      = id,
        name    = name,
        price   = price,
        stock   = stock,
        version = version,
    )
}