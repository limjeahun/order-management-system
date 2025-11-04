package event.oms.application.port.out.member

import event.oms.domain.model.member.Member

interface LoadMemberPort {
    fun findByUsername(username: String): Member?
    fun existsByUsername(username: String): Boolean
}