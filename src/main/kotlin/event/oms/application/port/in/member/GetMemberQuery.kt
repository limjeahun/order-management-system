package event.oms.application.port.`in`.member

import event.oms.domain.model.member.Member

interface GetMemberQuery {
    /**
     * 회원 아이디로 회원 정보 조회
     */
    fun getMemberByUsername(username: String): Member
}