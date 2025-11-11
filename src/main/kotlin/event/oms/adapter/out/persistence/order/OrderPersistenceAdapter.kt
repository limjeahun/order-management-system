package event.oms.adapter.out.persistence.order

import event.oms.adapter.out.persistence.order.mapper.toDomain
import event.oms.adapter.out.persistence.order.mapper.toOrderEntity
import event.oms.adapter.out.persistence.order.mapper.toOrderItemEntities
import event.oms.adapter.out.persistence.order.repository.OrderItemJpaRepository
import event.oms.adapter.out.persistence.order.repository.OrderJpaRepository
import event.oms.adapter.out.persistence.support.getOrThrow
import event.oms.application.port.out.order.LoadOrderPort
import event.oms.application.port.out.order.SaveOrderPort
import event.oms.domain.model.order.Order
import org.springframework.stereotype.Component

@Component
class OrderPersistenceAdapter(
    private val orderRepository    : OrderJpaRepository,
    private val orderItemRepository: OrderItemJpaRepository,
) : SaveOrderPort, LoadOrderPort {
    /**
     * 주문 생성
     */
    override fun save(order: Order): Order {
        // orderEntity 변환 및 저장
        val orderEntity      = order.toOrderEntity()
        val savedOrderEntity = orderRepository.save(orderEntity)
        val generatedOrderId = savedOrderEntity.id?: throw NoSuchElementException("주문번호를 찾을 수 없습니다.")
        // ItemsEntity 변환 및 저장
        val orderItemEntities     = order.toOrderItemEntities(generatedOrderId)
        val savedOrderItemsEntity = orderItemRepository.saveAll(orderItemEntities)
        // 자식 엔티티(OrderItem)들도 여기서 함께 저장 필요
        return savedOrderEntity.toDomain(savedOrderItemsEntity)
    }

    /**
     * 주문번호로 주문 조회
     */
    override fun findOrderById(orderId: Long): Order {
        val orderJpaEntity = orderRepository.getOrThrow(orderId)
        val orderItemJpaEntities = orderItemRepository.findByOrderId(orderId)
        // 조회된 엔티티들을 도메인 모델로 변환하여 반환
        return orderJpaEntity.toDomain(orderItemJpaEntities)
    }

    /**
     * 회원주문 목록 조회
     */
    override fun findAllByMemberId(memberId: Long): List<Order> {
        // 회원주문 목록 조회
        val orderJpaEntities = orderRepository.findAllByMemberId(memberId)
        // 추출된 ID 리스트를 사용해 모든 관련 주문 상품 조회
        val orderItemsMap = orderItemRepository.findByOrderIdIn(
            // 주문 ID 추출
            orderJpaEntities.map {
                it.id?: throw NoSuchElementException("주문번호를 찾을 수 없습니다.")
            }
        ).groupBy { it.orderId } // orderId를 기준으로 그룹화
        // 아이템 리스트 셋팅
        return orderJpaEntities.map { order ->
            order.toDomain(orderItemsMap[order.id]?: emptyList())
        }
    }

    /**
     * traceId로 주문 상세 조회
     */
    override fun findByTraceId(traceId: String): Order? {
        return orderRepository.findByTraceId(traceId)?.let {
            val orderItems = orderItemRepository.findByOrderId(
                it.id?: throw NoSuchElementException("주문번호를 찾을 수 없습니다.")
            )
            it.toDomain(orderItems)
        }
    }


}