package event.oms.domain.model.member

import java.time.LocalDateTime

/**
 * 회원 도메인 모델
 * (Serializable: Redis 캐시를 위해 추가)
 */
class Member(
    val id          : Long? = null,
    val username    : String,
    val passwordHash: String,
    val role        : Role,
    val createdAt   : LocalDateTime? = null
) {
    fun hasId(): Boolean = id != null
}