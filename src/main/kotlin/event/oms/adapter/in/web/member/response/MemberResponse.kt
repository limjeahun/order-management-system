package event.oms.adapter.`in`.web.member.response

import event.oms.domain.model.member.Member
import event.oms.domain.model.member.Role
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 응답")
data class MemberResponse(val id: Long, val username: String, val role: Role) {
    companion object {
        fun from(member: Member) = MemberResponse(
            id = member.id!!,
            username = member.username,
            role = member.role
        )
    }
}
