package event.oms.adapter.out.persistence.order

import event.oms.adapter.out.persistence.order.mapper.OrderMapper
import event.oms.adapter.out.persistence.order.repository.OrderItemJpaRepository
import event.oms.adapter.out.persistence.order.repository.OrderJpaRepository
import event.oms.application.port.out.order.LoadOrderPort
import event.oms.application.port.out.order.SaveOrderPort
import event.oms.domain.model.order.Order
import org.springframework.stereotype.Component

@Component
class OrderPersistenceAdapter(
    private val orderRepository    : OrderJpaRepository,
    private val orderItemRepository: OrderItemJpaRepository,
    private val orderMapper        : OrderMapper,
) : SaveOrderPort, LoadOrderPort {
    /**
     * 주문 생성
     */
    override fun save(order: Order): Order {
        val orderEntity = orderMapper.toEntity(order)
        val savedOrderEntity = orderRepository.save(orderEntity.first)
        val savedOrderItemsEntity  = orderItemRepository.saveAll(orderEntity.second)
        // 자식 엔티티(OrderItem)들도 여기서 함께 저장 필요
        return orderMapper.toDomain(savedOrderEntity, savedOrderItemsEntity)
    }

    /**
     * 주문번호로 주문 조회
     */
    override fun findOrderById(orderId: Long): Order? {
        val orderJpaEntity = orderRepository.findById(orderId).orElse(null) ?: return null
        val orderItemJpaEntities = orderItemRepository.findByOrderId(orderId)
        // 조회된 엔티티들을 도메인 모델로 변환하여 반환
        return orderMapper.toDomain(orderJpaEntity, orderItemJpaEntities)
    }


}