package event.oms.application.port.out.member

import event.oms.domain.model.member.Member

interface SaveMemberPort {
    fun save(member: Member): Member
}