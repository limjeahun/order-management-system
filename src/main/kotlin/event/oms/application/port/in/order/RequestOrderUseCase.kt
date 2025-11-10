package event.oms.application.port.`in`.order

/**
 * '주문 요청' 유스케이스
 */
interface RequestOrderUseCase {
    /**
     * 주문 요청
     */
    fun requestOrder(command: OrderCommand): String
}