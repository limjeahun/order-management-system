package event.oms.adapter.out.persistence.product.repository

import event.oms.adapter.out.persistence.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductJpaRepository : JpaRepository<ProductEntity, Long> {

}