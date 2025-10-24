package event.oms.application.port.`in`.order

interface GetOrderQuery {
    /**
     * 주문 상세 정보 조회
     */
    fun getOrder(orderId: Long): GetOrderResult
}