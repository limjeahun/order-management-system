package event.oms.adapter.`in`.web.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import event.oms.adapter.`in`.web.order.request.OrderItemRequest
import event.oms.adapter.`in`.web.order.request.OrderRequest
import event.oms.adapter.`in`.web.order.request.ReceiverInfoRequest
import event.oms.adapter.out.persistence.order.repository.OrderItemJpaRepository
import event.oms.adapter.out.persistence.order.repository.OrderJpaRepository
import event.oms.adapter.out.persistence.product.entity.ProductEntity
import event.oms.adapter.out.persistence.product.repository.ProductJpaRepository
import event.oms.domain.model.order.OrderStatus
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@AutoConfigureMockMvc
internal class OrderControllerIntegrationTest @Autowired constructor(
    private val mockMvc            : MockMvc,
    private val objectMapper       : ObjectMapper,
    private val orderRepository    : OrderJpaRepository,
    private val orderItemRepository: OrderItemJpaRepository,
    private val productRepository  : ProductJpaRepository,
) {
    @Test
    @DisplayName("주문 생성 API 호출 시: DB에 주문 및 주문 항목 정보가 정상적으로 저장된다")
    fun `newOrder - should save order and order items to database when API is called`() {
        // given
        val memberId = 1L
        // 컨트롤러에 전달할 요청 DTO
        val request = OrderRequest(
            memberId = memberId,
            items = listOf(
                OrderItemRequest(productId = 5312040000L, quantity = 1),
                OrderItemRequest(productId = 5312040001L, quantity = 1),
                OrderItemRequest(productId = 5312040002L, quantity = 1),
                OrderItemRequest(productId = 5312040003L, quantity = 1),
                OrderItemRequest(productId = 5312040004L, quantity = 1),
            ),
            receiverInfo = ReceiverInfoRequest(
                name = "곽철이",
                phone = "010-0000-0000",
                address = "서울시 강남구"
            )
        )

        // when - API 호출
        val result: MvcResult = mockMvc.perform(
            post("/api/v1/orders") // POST /api/v1/orders 요청
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated) // 201 Created 확인
        .andExpect(jsonPath("$.data.orderId").exists()) // 응답에 orderId 존재 확인
        .andReturn()

        // then
        val responseBody = result.response.contentAsString
        val createdOrderId = JsonPath.parse(responseBody).read("$.data.orderId", Long::class.javaObjectType)
        assertNotNull(createdOrderId, "응답에서 orderId를 추출")

        val savedOrderEntity = orderRepository.findById(createdOrderId)
        assertTrue(savedOrderEntity.isPresent, "ID: ${createdOrderId}로 주문을 찾아야 합니다.")
        savedOrderEntity.ifPresent { orderEntity ->
            assertEquals(memberId, orderEntity.memberId)
            assertEquals(OrderStatus.PENDING, orderEntity.status)
            assertEquals(request.receiverInfo.name, orderEntity.receiverInfo.receiverName)
        }

        val savedOrderItems = orderItemRepository.findByOrderId(createdOrderId)
        val savedTotalPrice = savedOrderItems.sumOf { it.price.multiply(BigDecimal.valueOf(it.quantity.toLong())) }
        assertEquals(5, savedOrderItems.size)



    }


}