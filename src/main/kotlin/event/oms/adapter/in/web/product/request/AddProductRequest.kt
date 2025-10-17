package event.oms.adapter.`in`.web.product.request

import event.oms.application.port.`in`.product.AddProductCommand
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class AddProductRequest(
    @field:NotEmpty(message = "상품코드는 필수입니다.")
    val id: Long,

    @field:NotEmpty(message = "상품명은 필수입니다.")
    val name: String,

    @field:NotNull(message = "가격은 필수입니다.")
    @field:DecimalMin(value = "0", inclusive = false, message = "가격은 0보다 커야 합니다.")
    val price: BigDecimal,

    @field:NotNull(message = "수량은 필수입니다.")
    @field:DecimalMin(value = "0", inclusive = false, message = "수량은 0보다 커야 합니다.")
    val stock: Int,
) {
    fun toCommand(): AddProductCommand {
        return AddProductCommand(
            id    = this.id,
            name  = this.name,
            price = this.price,
            stock = this.stock,
        )
    }
}
