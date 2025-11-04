package event.oms.application.port.`in`.member

data class SignUpCommand(
    val username            : String,
    val passwordNotEncrypted: String,
)
