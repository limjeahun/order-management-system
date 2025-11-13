package event.oms.adapter.`in`.web.order

import event.oms.adapter.`in`.web.common.BaseResponse
import event.oms.adapter.`in`.web.common.BaseResponse.Companion.toResponseEntity
import event.oms.adapter.`in`.web.order.request.OrderRequest
import event.oms.adapter.`in`.web.order.response.OrderResponse
import event.oms.adapter.`in`.web.payment.response.PaymentRequestResponse
import event.oms.application.port.`in`.payment.RequestPaymentUseCase
import event.oms.adapter.`in`.security.CustomUserDetails
import event.oms.adapter.`in`.web.order.response.OrderStatusResponse
import event.oms.application.port.`in`.order.*
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val requestOrderUseCase  : RequestOrderUseCase,
    private val getOrderQuery        : GetOrderQuery,
    private val getOrderListQuery    : GetOrderListQuery,
    private val getOrderByTraceQuery : GetOrderByTraceQuery,
    ): OrderSpec {
    @PostMapping
    override fun newOrder(
        @Valid @RequestBody request: OrderRequest,
        authentication: Authentication, // 인증 정보 주입

    ): ResponseEntity<BaseResponse<String>> {
        // 인증된 사용자의 ID (memberId) 추출
        val userDetails = authentication.principal as? CustomUserDetails
            ?: throw IllegalStateException("인증된 사용자의 정보가 없습니다.")
        // Application 계층의 Command 객체로 변환
        val command = request.toCommand(userDetails.id)
        // Inbound Port(UseCase) 호출하여 비즈니스 로직 실행
        val traceId = requestOrderUseCase.requestOrder(command)
        return BaseResponse.accepted(traceId).toResponseEntity()
    }

    @GetMapping("/{orderId}")
    override fun getOrderDetails(@PathVariable orderId: Long): ResponseEntity<BaseResponse<OrderResponse>> {
        // Inbound Port(Query) 호출
        val orderResult = getOrderQuery.getOrder(orderId)
        // Domain 모델을 응답 DTO로 변환하여 200 OK 응답 반환
        val response = OrderResponse.from(orderResult.order, orderResult.productNames)
        return BaseResponse.ok(response).toResponseEntity()
    }

    @GetMapping
    override fun getAllOrdersByMember(@RequestParam memberId: Long): ResponseEntity<BaseResponse<List<OrderResponse>>> {
        // Inbound Port(ListQuery) 호출
        val orderPairs = getOrderListQuery.getAllOrdersByMember(memberId)
        val responses = orderPairs.map { (order, productNames) ->
            OrderResponse.from(order, productNames)
        }
        return BaseResponse.ok(responses).toResponseEntity()
    }

    @GetMapping("/by-trace/{traceId}")
    override fun getOrderStatusByTrace(
        @PathVariable traceId: String,
        authentication: Authentication
    ): ResponseEntity<BaseResponse<OrderStatusResponse>> {
        // 인증된 사용자의 ID (memberId) 추출
        val userDetails = authentication.principal as? CustomUserDetails
            ?: throw IllegalStateException("인증된 사용자의 정보가 없습니다.")
        val result = getOrderByTraceQuery.getOrderSummaryByTrace(traceId, userDetails.id)
        val response = OrderStatusResponse.from(result)
        return BaseResponse.ok(response).toResponseEntity()
    }


}