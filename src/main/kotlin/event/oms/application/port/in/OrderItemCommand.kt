package event.oms.application.port.`in`

data class OrderItemCommand(
    val productId: Long,
    val quantity : Int
)
