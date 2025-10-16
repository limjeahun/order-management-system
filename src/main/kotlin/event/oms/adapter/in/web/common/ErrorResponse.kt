package event.oms.adapter.`in`.web.common

data class ErrorResponse(
    val message: String,
    val details: List<String>
)
