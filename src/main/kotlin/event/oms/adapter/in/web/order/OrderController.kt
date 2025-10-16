package event.oms.adapter.`in`.web.order

import event.oms.application.port.`in`.GetOrderQuery
import event.oms.application.port.`in`.OrderUseCase
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderUseCase: OrderUseCase,
    private val getOrderQuery: GetOrderQuery,

) {



}