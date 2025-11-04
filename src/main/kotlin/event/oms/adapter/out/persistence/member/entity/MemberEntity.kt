package event.oms.adapter.out.persistence.member.entity

import event.oms.adapter.out.persistence.support.AbstractJpaEntity
import event.oms.domain.model.member.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "member")
class MemberEntity(
    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime
): AbstractJpaEntity() {
}