package event.oms.application.service.member

import event.oms.application.port.`in`.member.SignUpCommand
import event.oms.application.port.`in`.member.SignUpUseCase
import event.oms.application.port.out.member.LoadMemberPort
import event.oms.application.port.out.member.SaveMemberPort
import event.oms.domain.model.member.Member
import event.oms.domain.model.member.Role
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SignUpService(
    private val loadMemberPort : LoadMemberPort,
    private val saveMemberPort : SaveMemberPort, // Redis 캐시(@CachePut) 포함
    private val passwordEncoder: PasswordEncoder,
): SignUpUseCase {
    @Transactional
    override fun signUp(command: SignUpCommand): Member {
        if (loadMemberPort.existsByUsername(command.username)) {
            throw IllegalArgumentException("이미 사용 중인 username 입니다.")
        }
        val hashedPassword = passwordEncoder.encode(command.passwordNotEncrypted)
        val newMember    = Member(
            username     = command.username,
            passwordHash = hashedPassword,
            role         = Role.ROLE_USER,
            createdAt    = LocalDateTime.now()
        )
        // DB 저장 + Redis 캐시
        return saveMemberPort.save(newMember)
    }
}