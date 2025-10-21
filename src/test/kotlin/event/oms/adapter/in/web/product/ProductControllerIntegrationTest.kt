package event.oms.adapter.`in`.web.product

import com.fasterxml.jackson.databind.ObjectMapper
import event.oms.adapter.`in`.web.product.request.AddProductRequest
import event.oms.adapter.out.persistence.product.repository.ProductJpaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
internal class ProductControllerIntegrationTest @Autowired constructor (
     private var mockMvc: MockMvc,
     private var objectMapper: ObjectMapper,
     private var productJpaRepository: ProductJpaRepository
) {

    @Test
    @DisplayName("상품 등록 API 호출 시: DB에 상품 정보가 정상적으로 저장된다")
    fun `addProduct - should save product to database when API is called`() {
        // given - 저장할 상품 데이터 준비
        val productId = 5312040002L
        val request = AddProductRequest(
            id    = productId,
            name  = "Marlboro Medium",
            price = BigDecimal("5000"),
            stock = 300,
        )

        // when - API 호출
        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated)
        .andExpect(
            jsonPath("$.data.productId")
                .value(productId)
        )

        // then - DB 저장 결과 검증
        val savedProductEntity = productJpaRepository.findById(productId)
        assertTrue(savedProductEntity.isPresent)
        savedProductEntity.ifPresent { entity ->
            assertEquals(request.name, entity.name)
            assertTrue(request.price.compareTo(entity.price) == 0)
            assertEquals(request.stock, entity.stock)
        }
    }


}