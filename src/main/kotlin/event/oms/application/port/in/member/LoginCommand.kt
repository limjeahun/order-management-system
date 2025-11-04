package event.oms.application.port.`in`.member

data class LoginCommand(
    val username            : String,
    val passwordNotEncrypted: String,
)
