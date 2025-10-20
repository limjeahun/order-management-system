package event.oms.adapter.out.persistence.product

import event.oms.adapter.out.persistence.product.mapper.ProductMapper
import event.oms.adapter.out.persistence.product.repository.ProductJpaRepository
import event.oms.application.port.out.product.LoadProductPort
import event.oms.application.port.out.product.SaveProductPort
import event.oms.domain.model.product.Product
import org.springframework.stereotype.Component

/**
 * 제품 영속성 어댑터
 */
@Component
class ProductPersistenceAdapter(
    private val productRepository: ProductJpaRepository,
    private val productMapper    : ProductMapper,
) : SaveProductPort, LoadProductPort {
    /**
     * 제품 등록
     */
    override fun save(product: Product): Product {
        val productEntity = productMapper.toEntity(product)
        val savedEntity   = productRepository.save(productEntity)
        return productMapper.toDomain(savedEntity)
    }

    /**
     * 제품 목록 조회
     */
    override fun findAllByIds(productIds: List<Long>): List<Product> {
        return productRepository.findAllById(productIds)
            .map { productMapper.toDomain(it) }
    }
}