package event.oms.application.port.`in`.order

data class OrderCommand(
    val memberId    : Long,
    val items       : List<OrderItemCommand>,
    val receiverInfo: ReceiverInfoCommand,
)
