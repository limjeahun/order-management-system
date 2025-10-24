package event.oms.config.feign

import com.fasterxml.jackson.databind.ObjectMapper
import feign.Logger
import feign.Response
import feign.codec.ErrorDecoder
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException

@Configuration
@EnableFeignClients(basePackages = ["event.oms.adapter.out.external"])
class FeignConfig(
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Feign 로깅 레벨 설정 (모든 클라이언트에 적용)
     * application.yml에서도 설정 가능: feign.client.config.default.loggerLevel: full
     */
    @Bean
    fun feignLoggerLevel(): Logger.Level {
        // 다른 옵션: NONE, BASIC, HEADERS
        return Logger.Level.FULL // 요청/응답 헤더 및 본문 모두 로깅
    }

    /**
     * 커스텀 에러 디코더 설정 (전역)
     * Feign 호출 시 발생하는 HTTP 에러(4xx, 5xx)를 처리하여
     * 특정 예외를 발생시키거나 복구 로직을 수행할 수 있습니다.
     */
    @Bean
    fun feignErrorDecoder(): ErrorDecoder {
        return CustomFeignErrorDecoder()
    }


    class CustomFeignErrorDecoder : ErrorDecoder {
        private val log = LoggerFactory.getLogger(javaClass)
        private val defaultDecoder = ErrorDecoder.Default()

        override fun decode(methodKey: String, response: Response): Exception {
            log.error("Feign Error - Method: {}, Status: {}, Reason: {}", methodKey, response.status(), response.reason())
            // 응답 본문 읽기
            val responseBody = try {
                response.body()?.asInputStream()?.bufferedReader()?.readText()
            } catch (e: IOException) {
                log.warn("Feign 에러 응답 본문 읽기 실패", e)
                null
            }
            log.error("Feign Error Body: {}", responseBody)

            // 특정 상태 코드에 따라 다른 예외 발생시키기
            when (response.status()) {
                400 -> return IllegalArgumentException("잘못된 요청입니다 (Feign: $methodKey) - $responseBody")
                404 -> return NoSuchElementException("리소스를 찾을 수 없습니다 (Feign: $methodKey) - $responseBody")
                500, 503 -> {
                    log.error("외부 API 서버 오류 발생 (Feign: $methodKey)")
                    return RuntimeException("외부 API 서버 오류 (Feign: $methodKey) - $responseBody")
                }
            }
            // 처리하지 않은 에러는 기본 디코더에게 위임
            return defaultDecoder.decode(methodKey, response)
        }
    }


}