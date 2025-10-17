package event.oms.application.port.`in`.order

data class ReceiverInfoCommand(
    val name   : String,
    val phone  : String,
    val address: String,
)
