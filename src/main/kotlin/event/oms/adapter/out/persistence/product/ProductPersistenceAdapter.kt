package event.oms.adapter.out.persistence.product

import event.oms.adapter.out.persistence.product.mapper.ProductMapper
import event.oms.adapter.out.persistence.product.repository.ProductJpaRepository
import event.oms.application.port.out.product.SaveProductPort
import event.oms.domain.model.product.Product
import org.springframework.stereotype.Component

@Component
class ProductPersistenceAdapter(
    private val productRepository: ProductJpaRepository,
    private val productMapper    : ProductMapper,
) : SaveProductPort {
    override fun save(product: Product): Product {
        val productEntity = productMapper.toEntity(product)
        val savedEntity   = productRepository.save(productEntity)
        return productMapper.toDomain(savedEntity)
    }
}