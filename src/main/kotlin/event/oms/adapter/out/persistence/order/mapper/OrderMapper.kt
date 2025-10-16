package event.oms.adapter.out.persistence.order.mapper

import event.oms.adapter.out.persistence.order.embeddable.ReceiverInfoEmbeddable
import event.oms.adapter.out.persistence.order.entity.OrderEntity
import event.oms.adapter.out.persistence.order.entity.OrderItemEntity
import event.oms.domain.model.order.Order
import event.oms.domain.model.order.OrderItem
import event.oms.domain.model.order.ReceiverInfo
import org.springframework.stereotype.Component

@Component
class OrderMapper {
    /**
     * 도메인 Order 객체를 JPA OrderJpaEntity 객체로 변환합니다.
     * DB에 저장하기 위해 사용됩니다.
     */
    fun toEntity(order: Order): Pair<OrderEntity, List<OrderItemEntity>> {
        val orderJpaEntity = OrderEntity(
            id = order.id,
            memberId = order.memberId,
            status = order.status,
            orderDate = order.orderDate,
            receiverInfo = ReceiverInfoEmbeddable(
                receiverName = order.receiverInfo.name,
                receiverPhone = order.receiverInfo.phone,
                receiverAddress = order.receiverInfo.address
            ),
        )

        val orderItemEntities = order.orderItems.map { domainItem ->
            OrderItemEntity(
                // id는 DB에서 자동 생성되므로 null
                orderId = orderJpaEntity.id ?: 0L, // 아직 ID가 없지만 관계 설정
                productId = domainItem.productId,
                price = domainItem.price,
                quantity = domainItem.quantity
            )
        }

        // Order 엔티티와 OrderItem 엔티티 리스트를 함께 반환
        return Pair(orderJpaEntity, orderItemEntities)
    }

    /**
     * JPA OrderJpaEntity 객체를 도메인 Order 객체로 변환합니다.
     * DB에서 조회한 데이터를 비즈니스 로직에서 사용하기 위해 사용됩니다.
     */
    fun toDomain(
        orderEntity: OrderEntity,
        orderItemEntities: List<OrderItemEntity>
    ): Order {
        return Order(
            id = orderEntity.id,
            memberId = orderEntity.memberId,
            status = orderEntity.status,
            orderDate = orderEntity.orderDate,
            orderItems = orderItemEntities.map { item ->
                OrderItem(
                    productId = item.productId,
                    price = item.price,
                    quantity = item.quantity
                )
            },
            receiverInfo = ReceiverInfo( // Embeddable 객체 매핑
                name = orderEntity.receiverInfo.receiverName,
                phone = orderEntity.receiverInfo.receiverPhone,
                address = orderEntity.receiverInfo.receiverAddress
            ),
        )
    }

}