package event.oms.application.port.`in`.product

import java.math.BigDecimal

data class AddProductCommand(
    val id   : Long,
    val name : String,
    val price: BigDecimal,
    val stock: Int,
)
