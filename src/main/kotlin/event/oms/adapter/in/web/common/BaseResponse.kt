package event.oms.adapter.`in`.web.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * 공통 API 응답 형식
 * @param T 응답 데이터의 타입
 * @property code HTTP 상태 코드
 * @property message 응답 메시지
 * @property data 실제 응답 데이터
 */
data class BaseResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
) {

    companion object {
        /**
         * 200 OK 응답을 생성합니다.
         * @param data 응답에 포함될 데이터
         */
        @JvmStatic // Java 코드와의 상호운용성을 위해 추가
        fun <T> ok(data: T?): BaseResponse<T> {
            return BaseResponse(
                code    = HttpStatus.OK.value(),
                message = "요청이 정상적으로 처리되었습니다.",
                data    = data
            )
        }

        /**
         * 201 Created 응답을 생성합니다.
         * @param data 응답에 포함될 데이터
         */
        @JvmStatic
        fun <T> created(data: T?): BaseResponse<T> {
            return BaseResponse(
                code    = HttpStatus.CREATED.value(),
                message = "리소스가 성공적으로 생성되었습니다.",
                data    = data
            )
        }
    }

    /**
     * BaseResponse 객체를 ResponseEntity로 변환하는 확장 함수
     */
    fun <T> BaseResponse<T>.toResponseEntity(): ResponseEntity<BaseResponse<T>> {
        return ResponseEntity
            .status(this.code)
            .body(this)
    }

}
