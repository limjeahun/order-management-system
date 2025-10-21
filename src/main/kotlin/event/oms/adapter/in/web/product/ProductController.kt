package event.oms.adapter.`in`.web.product

import event.oms.adapter.`in`.web.common.BaseResponse
import event.oms.adapter.`in`.web.common.BaseResponse.Companion.toResponseEntity
import event.oms.adapter.`in`.web.product.request.AddProductRequest
import event.oms.adapter.`in`.web.product.response.ProductResponse
import event.oms.application.port.`in`.product.AddProductUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val addProductUseCase: AddProductUseCase
) {

    @PostMapping
    fun addProduct(@Valid @RequestBody request: AddProductRequest): ResponseEntity<BaseResponse<ProductResponse>> {
        val command = request.toCommand()
        val createdProduct = addProductUseCase.addProduct(command)
        val response = ProductResponse.from(createdProduct)
        return BaseResponse.created(response).toResponseEntity()
    }
}