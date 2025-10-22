package event.oms.adapter.`in`.web.product

import event.oms.adapter.`in`.web.common.BaseResponse
import event.oms.adapter.`in`.web.common.BaseResponse.Companion.toResponseEntity
import event.oms.adapter.`in`.web.product.request.AddProductRequest
import event.oms.adapter.`in`.web.product.request.UpdateProductRequest
import event.oms.adapter.`in`.web.product.response.ProductResponse
import event.oms.application.port.`in`.product.AddProductUseCase
import event.oms.application.port.`in`.product.GetProductListQuery
import event.oms.application.port.`in`.product.UpdateProductUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val addProductUseCase   : AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val getProductListQuery : GetProductListQuery
): ProductSpec {

    @PostMapping
    override fun addProduct(@Valid @RequestBody request: AddProductRequest): ResponseEntity<BaseResponse<ProductResponse>> {
        val command = request.toCommand()
        val createdProduct = addProductUseCase.addProduct(command)
        val response = ProductResponse.from(createdProduct)
        return BaseResponse.created(response).toResponseEntity()
    }

    @PutMapping("/{productId}")
    override fun updateProduct(
        @PathVariable productId: Long,
        @Valid @RequestBody request: UpdateProductRequest
    ): ResponseEntity<BaseResponse<ProductResponse>> {
        val command = request.toCommand(productId)
        val updateProduct = updateProductUseCase.updateProduct(command)
        val response = ProductResponse.from(updateProduct)
        return BaseResponse.ok(response).toResponseEntity()
    }

    @GetMapping
    override fun getAllProducts(): ResponseEntity<BaseResponse<List<ProductResponse>>> {
        val products  = getProductListQuery.getAllProducts()
        val responses = products.map { ProductResponse.from(it) }
        return BaseResponse.ok(responses).toResponseEntity()
    }

}