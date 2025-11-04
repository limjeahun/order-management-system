package event.oms.adapter.out.persistence.member

import event.oms.adapter.out.persistence.member.mapper.toDomain
import event.oms.adapter.out.persistence.member.mapper.toEntity
import event.oms.adapter.out.persistence.member.repository.MemberJpaRepository
import event.oms.application.port.out.member.LoadMemberPort
import event.oms.application.port.out.member.SaveMemberPort
import event.oms.domain.model.member.Member
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
class MemberPersistenceAdapter(
    private val memberRepository: MemberJpaRepository
): LoadMemberPort, SaveMemberPort {
    companion object { const val MEMBER_CACHE_NAME = "member" }

    @Cacheable(value = [MEMBER_CACHE_NAME], key = "#username")
    override fun findByUsername(username: String): Member? {
        return memberRepository.findByUsername(username)?.toDomain()
    }

    override fun existsByUsername(username: String): Boolean {
        return memberRepository.existsByUsername(username)
    }

    @CachePut(value = [MEMBER_CACHE_NAME], key = "#member.username")
    override fun save(member: Member): Member {
        val entity = member.toEntity()
        val savedEntity = memberRepository.save(entity)
        return savedEntity.toDomain()
    }
}