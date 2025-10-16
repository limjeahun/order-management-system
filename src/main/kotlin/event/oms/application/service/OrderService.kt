package event.oms.application.service

import event.oms.application.port.`in`.OrderCommand
import event.oms.application.port.`in`.OrderUseCase
import event.oms.application.port.out.SaveOrderPort
import event.oms.domain.model.order.Order
import event.oms.domain.model.order.OrderItem
import event.oms.domain.model.order.OrderStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
class OrderService(
    private val saveOrderPort: SaveOrderPort,
) : OrderUseCase {
    override fun order(command: OrderCommand): Order {

        val items = command.items.map {
            OrderItem(
                productId = it.productId,
                price     = BigDecimal.ZERO, // 임시 가격
                quantity  = it.quantity
            )
        }

        var newOrder = Order(
            memberId   = command.memberId,
            orderItems = items,
            status     = OrderStatus.PENDING,
            orderDate  = LocalDateTime.now()
        )

        return saveOrderPort.save(newOrder)
    }

}