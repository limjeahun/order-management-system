package event.oms.application.service.order

import event.oms.application.port.`in`.order.GetOrderListQuery
import event.oms.application.port.out.order.LoadOrderPort
import event.oms.application.port.out.product.LoadProductPort
import event.oms.domain.model.order.Order
import event.oms.domain.model.product.Product
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetOrderListService(
    private val loadOrderPort  : LoadOrderPort,
    private val loadProductPort: LoadProductPort,
) : GetOrderListQuery {
    /**
     * 회원주문 목록 조회
     */
    override fun getAllOrdersByMember(memberId: Long): List<Pair<Order, Map<Long, String>>> {
        // 회원의 모든 주문 조회
        val orders = loadOrderPort.findAllByMemberId(memberId)
        // 주문내역이 없을 시
        if (orders.isEmpty()) {
            return emptyList()
        }
        // 모든 주문 항목에서 필요한 상품 ID 추출
        val allProductIds = orders.flatMap {
            it.orderItems.map { item -> item.productId }
        }.distinct()
        // 상품 정보 조회
        val productMap = if (allProductIds.isNotEmpty()) {
            loadProductPort.findAllByIds(allProductIds).associateBy { it.id }
        } else {
            emptyMap()
        }
        return getAllOrdersWithProductNames(orders, productMap)
    }

    /**
     * 주문별로 Pair(Order, 상품명 Map) 생성하여 리스트로 반환
     */
    private fun getAllOrdersWithProductNames(orders: List<Order>, productMap: Map<Long, Product>): List<Pair<Order, Map<Long, String>>> {
        return orders.map { order ->
            val productNamesMapForOrder = order.orderItems
                .mapNotNull { item -> productMap[item.productId] }
                .associate { product -> product.id to product.name }
            Pair(order, productNamesMapForOrder)
        }
    }

}