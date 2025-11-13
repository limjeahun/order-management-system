package event.oms.adapter.`in`.web.order

import event.oms.adapter.`in`.web.common.BaseResponse
import event.oms.adapter.`in`.web.order.request.OrderRequest
import event.oms.adapter.`in`.web.order.response.OrderResponse
import event.oms.adapter.`in`.web.order.response.OrderStatusResponse
import event.oms.adapter.`in`.web.payment.response.PaymentRequestResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication


@Tag(name = "주문 API", description = "주문 관련 API")
interface OrderSpec {
    @Operation(summary = "신규 주문 등록", description = "새로운 주문 정보를 시스템에 등록합니다.")
    fun newOrder(request: OrderRequest, authentication: Authentication): ResponseEntity<BaseResponse<String>>

    @Operation(summary = "주문 상세 정보 조회", description = "시스템에 등록된 주문상세 정보를 조회합니다.")
    fun getOrderDetails(
        @Parameter(description = "조회할 주문 ID")
        orderId: Long
    ): ResponseEntity<BaseResponse<OrderResponse>>

    @Operation(summary = "회원 주문 목록 정보 조회", description = "시스템에 등록된 주문목록 정보를 조회합니다.")
    fun getAllOrdersByMember(
        @Parameter(description = "조회할 회원 ID")
        memberId: Long
    ): ResponseEntity<BaseResponse<List<OrderResponse>>>

    @Operation(summary = "신규 주문 상태 조회", description = "시스템에 등록된 주문 정보를 확인 후 주문 상태 정보를 조회합니다.")
    fun getOrderStatusByTrace(
        @Parameter(description = " 주문 추적 ID")
        traceId: String,
        authentication: Authentication
    ): ResponseEntity<BaseResponse<OrderStatusResponse>>
}