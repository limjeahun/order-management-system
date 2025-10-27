package event.oms.adapter.out.persistence.order.mapper

import event.oms.adapter.out.persistence.order.embeddable.ReceiverInfoEmbeddable
import event.oms.adapter.out.persistence.order.entity.OrderEntity
import event.oms.adapter.out.persistence.order.entity.OrderItemEntity
import event.oms.domain.model.order.Order
import event.oms.domain.model.order.OrderItem
import event.oms.domain.model.order.ReceiverInfo

/**
 * 도메인 Order 객체를 JPA OrderJpaEntity 객체로 변환하는 확장 함수
 * @return 주문 Entity
 */
fun Order.toOrderEntity(): OrderEntity {
    return OrderEntity(
        id           = id,
        memberId     = memberId,
        status       = status,
        orderDate    = orderDate,
        receiverInfo = ReceiverInfoEmbeddable(
            receiverName    = receiverInfo.name,
            receiverPhone   = receiverInfo.phone,
            receiverAddress = receiverInfo.address
        )
    )
}

/**
 * 도메인 Order 객체를 JPA List<OrderItemEntity> 객체로 변환하는 확장 함수
 * @param orderId 주문번호
 * @return 주문 상품정보 Entity
 */
fun Order.toOrderItemEntities(orderId: Long): List<OrderItemEntity> {
    return orderItems.map {
        OrderItemEntity(
            orderId = orderId,
            productId = it.productId,
            price     = it.price,
            quantity  = it.quantity,
        )
    }
}

/**
 * JPA OrderJpaEntity 객체를 도메인 Order 객체로 변환합니다.
 * @param orderItemEntities 주문 상품정보 목록
 * @return 주문 도메인 모델
 */
fun OrderEntity.toDomain(orderItemEntities: List<OrderItemEntity>): Order {
    return Order(
        id         = id,
        memberId   = memberId,
        status     = status,
        orderDate  = orderDate,
        orderItems = orderItemEntities.map { item ->
            OrderItem(
                productId = item.productId,
                price = item.price,
                quantity = item.quantity
            )
        },
        receiverInfo = ReceiverInfo(
            name    = receiverInfo.receiverName,
            phone   = receiverInfo.receiverPhone,
            address = receiverInfo.receiverAddress,
        )
    )
}