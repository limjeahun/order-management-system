package event.oms.application.service.order

import event.oms.application.port.`in`.order.GetOrderQuery
import event.oms.application.port.out.order.LoadOrderPort
import event.oms.application.port.out.product.LoadProductPort
import event.oms.domain.model.order.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetOrderService(
    private val loadOrderPort  : LoadOrderPort,
    private val loadProductPort: LoadProductPort
) : GetOrderQuery {
    /**
     * 주문 상세 정보 조회
     */
    override fun getOrder(orderId: Long): Pair<Order, Map<Long, String>> {
        // 1. 주문 상세 정보 조회
        val order = loadOrderPort.findOrderById(orderId)
            ?: throw NoSuchElementException("ID가 ${orderId}인 주문을 찾을 수 없습니다.")
        // 2. 관련 상품 정보 조회
        val productIds = order.orderItems.map { it.productId }
        val productNamesMap = if (productIds.isNotEmpty()) {
            loadProductPort.findAllByIds(productIds)
                .associate { it.id to it.name }
        } else {
            emptyMap()
        }
        // 3. Order 객체와 상품명 Map을 Pair로 묶어 반환
        return Pair(order, productNamesMap)
    }
}