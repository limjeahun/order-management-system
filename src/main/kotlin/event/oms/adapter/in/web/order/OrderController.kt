package event.oms.adapter.`in`.web.order

import event.oms.adapter.`in`.web.common.BaseResponse
import event.oms.adapter.`in`.web.common.BaseResponse.Companion.toResponseEntity
import event.oms.adapter.`in`.web.order.request.OrderRequest
import event.oms.adapter.`in`.web.order.response.OrderResponse
import event.oms.application.port.`in`.order.GetOrderListQuery
import event.oms.application.port.`in`.order.GetOrderQuery
import event.oms.application.port.`in`.order.OrderUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
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
    private val orderUseCase     : OrderUseCase,
    private val getOrderQuery    : GetOrderQuery,
    private val getOrderListQuery: GetOrderListQuery,
    ): OrderSpec {
    @PostMapping
    override fun newOrder(@Valid @RequestBody request: OrderRequest): ResponseEntity<BaseResponse<OrderResponse>> {
        // 1. Application 계층의 Command 객체로 변환
        val command = request.toCommand()
        // 2. Inbound Port(UseCase) 호출하여 비즈니스 로직 실행
        val createdOrder = orderUseCase.order(command)
        // 3. Domain 모델을 응답 DTO로 변환
        val response = OrderResponse.from(createdOrder)
        return BaseResponse.created(response).toResponseEntity()
    }

    @GetMapping("/{orderId}")
    override fun getOrderDetails(@PathVariable orderId: Long): ResponseEntity<BaseResponse<OrderResponse>> {
        // 1. Inbound Port(Query) 호출
        val (order, productNames) = getOrderQuery.getOrder(orderId)
        // 2. Domain 모델을 응답 DTO로 변환하여 200 OK 응답 반환
        val response = OrderResponse.from(order, productNames)
        return BaseResponse.ok(response).toResponseEntity()
    }

    @GetMapping
    override fun getAllOrdersByMember(@RequestParam memberId: Long): ResponseEntity<BaseResponse<List<OrderResponse>>> {
        // 1. Inbound Port(ListQuery) 호출
        val orderPairs = getOrderListQuery.getAllOrdersByMember(memberId)
        val responses = orderPairs.map { (order, productNames) ->
            OrderResponse.from(order, productNames)
        }
        return BaseResponse.ok(responses).toResponseEntity()
    }

}