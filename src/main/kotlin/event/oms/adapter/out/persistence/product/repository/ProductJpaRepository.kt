package event.oms.adapter.out.persistence.product.repository

import event.oms.adapter.out.persistence.product.entity.ProductEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository

@Repository
interface ProductJpaRepository : JpaRepository<ProductEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findAllByIdIn(ids: Collection<Long>): List<ProductEntity>

}