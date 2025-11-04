package event.oms.adapter.out.persistence.member.mapper

import event.oms.adapter.out.persistence.member.entity.MemberEntity
import event.oms.domain.model.member.Member
import java.time.LocalDateTime

/**
 * 도메인 Member -> MemberEntity
 */
fun Member.toEntity(): MemberEntity {
    return MemberEntity(
        username  = this.username,
        password  = this.passwordHash,
        role      = this.role,
        createdAt = this.createdAt ?: LocalDateTime.now() // 생성 시점 보장
    )
}

/**
 * MemberEntity -> 도메인 Member
 */
fun MemberEntity.toDomain(): Member {
    return Member(
        id           = this.id,
        username     = this.username,
        passwordHash = this.password,
        role         = this.role,
        createdAt    = this.createdAt
    )
}