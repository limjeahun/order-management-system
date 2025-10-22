package event.oms.adapter.`in`.web.product

import event.oms.adapter.`in`.web.common.BaseResponse
import event.oms.adapter.`in`.web.product.request.AddProductRequest
import event.oms.adapter.`in`.web.product.request.UpdateProductRequest
import event.oms.adapter.`in`.web.product.response.ProductResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity


@Tag(name = "제품 API", description = "제품 관련 API")
interface ProductSpec {
    @Operation(summary = "신규 제품 등록", description = "새로운 제품 정보를 시스템에 등록합니다.")
    fun addProduct(request: AddProductRequest): ResponseEntity<BaseResponse<ProductResponse>>

    @Operation(summary = "제품 수정", description = "시스템에 등록된 제품 정보를 수정합니다.")
    fun updateProduct(productId: Long, request: UpdateProductRequest): ResponseEntity<BaseResponse<ProductResponse>>

    @Operation(summary = "제품 목록 조회", description = "시스템에 등록된 제품 목록 정보를 죄회합니다.")
    fun getAllProducts(): ResponseEntity<BaseResponse<List<ProductResponse>>>
}