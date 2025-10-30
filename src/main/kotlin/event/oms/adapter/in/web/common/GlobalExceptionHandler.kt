package event.oms.adapter.`in`.web.common

import event.oms.adapter.out.external.payment.exception.PaymentApiClientException
import event.oms.adapter.out.external.payment.exception.PaymentNotFoundException
import event.oms.adapter.out.external.payment.exception.PaymentServerException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    // @Valid 유효성 검사 실패 시
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map {
            "${it.field}: ${it.defaultMessage}"
        }
        val errorResponse = ErrorResponse("Validation Failed", errors)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    // 데이터를 찾지 못했을 경우 (Service 계층에서 NoSuchElementException 발생 시)
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementExceptions(ex: NoSuchElementException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("Resource Not Found", listOf(ex.message ?: "No such element"))
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("The input value is invalid.", listOf(ex.message ?: "Invalid input"))
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    // Feign - 404 (Not Found)
    @ExceptionHandler(PaymentNotFoundException::class)
    fun handlePaymentNotFoundException(ex: PaymentNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("Resource Not Found (Payment)", listOf(ex.message ?: "Payment resource not found"))
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    // Feign - 4xx (Client Error, 404 제외)
    @ExceptionHandler(PaymentApiClientException::class)
    fun handlePaymentApiClientException(ex: PaymentApiClientException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("Bad Request (Payment API)", listOf(ex.message ?: "Invalid request to payment API"))
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    // Feign - 5xx (Server Error)
    @ExceptionHandler(PaymentServerException::class)
    fun handlePaymentServerException(ex: PaymentServerException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("Payment Server Error", listOf(ex.message ?: "External payment server is unavailable"))
        // 503 Service Unavailable (서비스 장애) 또는 500 Internal Server Error 반환
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse)
    }

    // 그 외 일반적인 서버 오류
    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("Internal Server Error", listOf(ex.message ?: "An unexpected error occurred"))
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}