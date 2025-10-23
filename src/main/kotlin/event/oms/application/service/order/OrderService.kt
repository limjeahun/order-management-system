package event.oms.application.service.order

import event.oms.application.port.`in`.order.OrderCommand
import event.oms.application.port.`in`.order.OrderUseCase
import event.oms.application.port.out.order.SaveOrderPort
import event.oms.application.port.out.product.LoadProductPort
import event.oms.application.port.out.product.SaveProductPort
import event.oms.domain.model.order.Order
import event.oms.domain.model.order.OrderStatus
import event.oms.domain.model.order.ReceiverInfo
import event.oms.domain.model.product.Product
import event.oms.domain.service.order.OrderPriceCalculator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
@Transactional
class OrderService(
    private val saveOrderPort       : SaveOrderPort,
    private val loadProductPort     : LoadProductPort,
    private val saveProductPort     : SaveProductPort,
    private val orderPriceCalculator: OrderPriceCalculator,
) : OrderUseCase {
    override fun order(command: OrderCommand): Order {
        // 1. 필요한 외부 데이터(상품) 조회
        val productIds = command.items.map { it.productId }
        val products   = loadProductPort.findAllByIdsForUpdate(productIds)
        // 2. 가격, 재고 검증 데이터 객체 생성
        val itemRequests = command.items.map {
            OrderPriceCalculator.ItemRequest(it.productId, it.quantity)
        }
        // 3. 재고 검증 및 가격 확인
        val orderItems = orderPriceCalculator.calculateOrderItems(itemRequests, products)
        log.info("--- 주문 정보: orderItems = {} ---", orderItems)
        // 4. 실제 재고 차감 및 변경된 상품 리스트 생성
        val updatedProducts = orderItems.mapNotNull { item ->
            val product = products.find { it.id == item.productId }
            product?.apply {
                decreaseStock(item.quantity)
                log.debug("상품 재고 차감: productId={}, quantity={}, remainingStock={}", item.productId, item.quantity, stock)
            } // apply는 자기 자신(product)을 반환
        }

        // 5. 변경된 상품 정보 한 번에 저장
        if (updatedProducts.isNotEmpty()) {
            saveProductPort.saveAll(updatedProducts)
        }

        // 4. 주문 생성
        val newOrder = Order(
            memberId     = command.memberId,
            orderItems   = orderItems,
            status       = OrderStatus.PENDING,
            orderDate    = LocalDateTime.now(),
            receiverInfo =  ReceiverInfo(
                name    = command.receiverInfo.name,
                phone   = command.receiverInfo.phone,
                address = command.receiverInfo.address
            )
        )
        // 5. 주문 저장
        return saveOrderPort.save(newOrder)
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderService::class.java)
    }

}