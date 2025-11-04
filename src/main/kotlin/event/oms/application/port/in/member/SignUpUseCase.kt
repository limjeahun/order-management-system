package event.oms.application.port.`in`.member

import event.oms.domain.model.member.Member

interface SignUpUseCase {
    fun signUp(command: SignUpCommand): Member
}