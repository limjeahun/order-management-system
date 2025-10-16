package event.oms.adapter.`in`.web.common

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

    // 그 외 일반적인 서버 오류
    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("Internal Server Error", listOf(ex.message ?: "An unexpected error occurred"))
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}