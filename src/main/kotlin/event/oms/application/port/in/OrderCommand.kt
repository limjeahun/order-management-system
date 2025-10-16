package event.oms.application.port.`in`

data class OrderCommand(
    val memberId    : Long,
    val items       : List<OrderItemCommand>,
    val receiverInfo: ReceiverInfoCommand,
)
