package event.oms.config.feign

import event.oms.adapter.out.external.payment.exception.PaymentApiClientException
import event.oms.adapter.out.external.payment.exception.PaymentNotFoundException
import event.oms.adapter.out.external.payment.exception.PaymentServerException
import feign.Response
import feign.codec.ErrorDecoder
import org.slf4j.LoggerFactory
import java.io.IOException

class PaymentApiErrorDecoder: ErrorDecoder {

    private val log = LoggerFactory.getLogger(javaClass)
    private val defaultDecoder = ErrorDecoder.Default()

    override fun decode(methodKey: String, response: Response): Exception {
        val responseBody = readResponseBody(response)
        log.error("Payment API Error - Method: {}, Status: {}, Body: {}", methodKey, response.status(), responseBody)
        return when (response.status()) {
            404 -> PaymentNotFoundException("결제 정보를 찾을 수 없습니다. (응답: $responseBody)")
            500, 503 -> PaymentServerException("결제 API 서버 내부 오류가 발생했습니다. (응답: $responseBody)")
            in 400..499 -> PaymentApiClientException("결제 API 클라이언트 오류 (상태: ${response.status()}, 응답: $responseBody)")
            // 그 외 처리하지 않은 오류는 기본 디코더에 위임
            else -> defaultDecoder.decode(methodKey, response)
        }
    }

    /**
     * 응답 본문을 안전하게 읽는 Helper 함수
     */
    private fun readResponseBody(response: Response): String? {
        return try {
            response.body()?.asInputStream()?.use { it.readBytes().toString(Charsets.UTF_8) }
        } catch (e: IOException) {
            log.warn("Feign 에러 응답 본문 읽기 실패", e)
            null
        }
    }

}