package event.oms.adapter.`in`.web.product

import com.fasterxml.jackson.databind.ObjectMapper
import event.oms.UseCaseTestConfiguration
import event.oms.adapter.`in`.web.product.request.AddProductRequest
import event.oms.application.port.`in`.product.AddProductUseCase
import event.oms.domain.model.product.Product
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal


@WebMvcTest(ProductController::class) // 테스트할 컨트롤러 지정
@Import(UseCaseTestConfiguration::class)
internal class ProductControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc // HTTP 요청 시뮬레이션 객체

    @Autowired
    private lateinit var objectMapper: ObjectMapper // JSON 직렬화/역직렬화

    @Autowired // ProductController가 의존하는 UseCase를 Mock 객체로 대체
    private lateinit var addProductUseCase: AddProductUseCase

    @Test
    @DisplayName("상품 등록 성공 시: 201 Created 상태코드와 상품 ID를 반환한다")
    fun `addProduct - should return 201 Created with product ID when product is added successfully`() {
        // given
        val productId = 1001L
        val request = AddProductRequest(
            id    = productId,
            name  = "테스트 상품",
            price = BigDecimal("15000.00"),
            stock = 50,
        )
        val expectedCommand = request.toCommand()

        val createdProductDomain = Product(
            id = productId,
            name = request.name,
            price = request.price,
            stock = request.stock,
        )
        given(addProductUseCase.addProduct(expectedCommand)).willReturn(createdProductDomain)

        // when
        val resultActions = mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
            .andExpect(jsonPath("$.message").value("리소스가 성공적으로 생성되었습니다."))
            .andExpect(jsonPath("$.data.productId").value(productId))

        then(addProductUseCase).should().addProduct(expectedCommand)
    }

    @Test
    @DisplayName("상품 등록 시 유효성 검증 실패: 400 Bad Request 상태코드를 반환한다")
    fun `addProduct - should return 400 Bad Request when request is invalid`() {
        // given
        val invalidRequest = AddProductRequest(
            id = 456L,
            name = "",
            price = BigDecimal("50.00"),
            stock = 5
        )

        // when
        val resultActions = mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )

        // then
        resultActions
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("Validation Failed"))
            .andExpect(jsonPath("$.details[0]").value("name: 상품명은 필수입니다."))
    }

}