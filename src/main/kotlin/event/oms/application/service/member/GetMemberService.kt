package event.oms.application.service.member

import event.oms.application.port.`in`.member.GetMemberQuery
import event.oms.application.port.out.member.LoadMemberPort
import event.oms.domain.model.member.Member
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class GetMemberService(
    private val loadMemberPort: LoadMemberPort
): GetMemberQuery {
    override fun getMemberByUsername(username: String): Member {
        return loadMemberPort.findByUsername(username)
            ?: throw UsernameNotFoundException("해당 회원 아이디를 찾지 못헀습니다.: $username")
    }
}