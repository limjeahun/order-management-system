package event.oms.application.port.`in`.product

import java.math.BigDecimal

/**
 * 제품 수정에 필요한 Command 객체
 */
data class UpdateProductCommand(
    val id   : Long,
    val name : String,
    val price: BigDecimal,
    val stock: Int,
)
