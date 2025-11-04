package event.oms.adapter.out.persistence.member.repository

import event.oms.adapter.out.persistence.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberJpaRepository: JpaRepository<MemberEntity, Long> {
    fun findByUsername(username: String): MemberEntity?
    fun existsByUsername(username: String): Boolean
}