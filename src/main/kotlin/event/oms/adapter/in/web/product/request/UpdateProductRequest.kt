package event.oms.adapter.`in`.web.product.request

import event.oms.application.port.`in`.product.UpdateProductCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal


@Schema(description = "제품수정 요청 정보")
data class UpdateProductRequest(
    @field:NotEmpty(message = "제품명은 필수입니다.")
    val name: String,

    @field:NotNull(message = "가격은 필수입니다.")
    @field:DecimalMin(value = "0", inclusive = false, message = "가격은 0보다 커야 합니다.")
    val price: BigDecimal,

    @field:NotNull(message = "수량은 필수입니다.")
    @field:Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    val stock: Int,
) {
    /**
     * UpdateProductCommand 변환
     */
    fun toCommand(id: Long): UpdateProductCommand {
        return UpdateProductCommand(
            id    = id,
            name  = name,
            price = price,
            stock = stock,
        )
    }
}
