package event.oms.adapter.`in`.web.interceptor

import event.oms.application.port.`in`.waitlist.WaitListUseCase
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class WaitingRoomInterceptor(
    private val waitListUseCase: WaitListUseCase
): HandlerInterceptor {

    override fun preHandle(
        request : HttpServletRequest,
        response: HttpServletResponse,
        handler : Any,
    ): Boolean {
        val auth = SecurityContextHolder.getContext().authentication
        // 인증 실패 시
        if (auth == null || !auth.isAuthenticated) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            return false
        }

        val userId = auth.name // (CustomUserDetails.username)

        if (waitListUseCase.isAllowed(userId)) {
            // 1. [진입 허용]
            return true
        } else {
            // 2. [진입 차단] 대기열로 보냄
            val rank = waitListUseCase.addToQueue(userId)

            response.status = HttpStatus.TOO_MANY_REQUESTS.value() // HTTP 429
            response.contentType = "application/json;charset=UTF-8"
            response.writer.write("""
                {
                    "code": 429,
                    "message": "현재 접속자가 많아 대기 중입니다. (예상 순번: $rank)",
                    "data": { "waitingRank": $rank }
                }
            """.trimIndent())
            return false // 컨트롤러 진입 차단
        }

    }
}