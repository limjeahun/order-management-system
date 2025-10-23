package event.oms.adapter.`in`.web.order

import event.oms.adapter.`in`.web.common.BaseResponse
import event.oms.adapter.`in`.web.order.request.OrderRequest
import event.oms.adapter.`in`.web.order.response.OrderResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity


@Tag(name = "주문 API", description = "주문 관련 API")
interface OrderSpec {
    @Operation(summary = "신규 주문 등록", description = "새로운 주문 정보를 시스템에 등록합니다.")
    fun newOrder(request: OrderRequest): ResponseEntity<BaseResponse<OrderResponse>>

    @Operation(summary = "주문상세 정보 조회", description = "시스템에 등록된 주문상세 정보를 조회합니다.")
    fun getOrderDetails(orderId: Long): ResponseEntity<BaseResponse<OrderResponse>>

    @Operation(summary = "회원 주문목록 정보 조회", description = "시스템에 등록된 주문목록 정보를 조회합니다.")
    fun getAllOrders(memberId: Long): ResponseEntity<BaseResponse<List<OrderResponse>>>
}