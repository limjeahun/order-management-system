package event.oms.adapter.out.persistence.product

import event.oms.adapter.out.persistence.product.mapper.toDomain
import event.oms.adapter.out.persistence.product.mapper.toEntity
import event.oms.adapter.out.persistence.product.repository.ProductJpaRepository
import event.oms.adapter.out.persistence.support.findThenMap
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
) : SaveProductPort, LoadProductPort {
    /**
     * 제품 등록
     */
    override fun save(product: Product): Product {
        val productEntity = product.toEntity()
        val savedEntity   = productRepository.save(productEntity)
        return savedEntity.toDomain()
    }

    /**
     * 여러 제품 정보를 등록
     */
    override fun saveAll(products: List<Product>): List<Product> {
        val productEntities = products.map { it.toEntity() }
        val savedEntities   = productRepository.saveAll(productEntities)
        return savedEntities.map { it.toDomain() }
    }

    /**
     * 제품 목록 조회
     */
    override fun findAllByIds(productIds: List<Long>): List<Product> {
        return productRepository.findAllById(productIds)
            .map { it.toDomain() }
    }

    /**
     * 제품 상세 조회
     */
    override fun findById(productId: Long): Product? {
        return productRepository.findThenMap(productId) {
            it.toDomain() // 조회 성공 시 도메인 객체로 변환
        }
    }

    /**
     * 제품 목록 조회
     */
    override fun findAll(): List<Product> {
        return productRepository.findAll()
            .map { it.toDomain() }
    }

    /**
     * 제품 목록 조회 호출 시 락 적용 메서드 사용
     */
    override fun findAllByIdsForUpdate(productIds: List<Long>): List<Product> {
        return productRepository.findAllByIdIn(productIds)
            .map { it.toDomain() }
    }

}