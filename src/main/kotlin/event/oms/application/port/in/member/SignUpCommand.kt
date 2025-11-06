package event.oms.application.port.`in`.member

import event.oms.domain.model.member.Role

data class SignUpCommand(
    val username            : String,
    val passwordNotEncrypted: String,
    val role                : Role,
)
