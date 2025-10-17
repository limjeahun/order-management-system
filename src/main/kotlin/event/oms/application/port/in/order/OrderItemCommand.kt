package event.oms.application.port.`in`.order

data class OrderItemCommand(
    val productId: Long,
    val quantity : Int
)
