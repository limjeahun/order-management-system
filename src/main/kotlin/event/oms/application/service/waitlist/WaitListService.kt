package event.oms.application.service.waitlist

import event.oms.application.port.`in`.waitlist.WaitListUseCase
import event.oms.application.port.out.waitlist.WaitListPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class WaitListService(
    private val waitListPort: WaitListPort
): WaitListUseCase {
    @Value("\${promotion.max-active-users:1000}")
    private val MAX_ACTIVE_USERS: Long = 1000L

    override fun isAllowed(userId: String): Boolean {
        // 1. 중복 입장 방지
        if (waitListPort.isUserActive(userId)) {
            return true
        }
        // 2. 활성 사용자 수가 임계값 미만 확인
        if (waitListPort.getActiveUserCount() < MAX_ACTIVE_USERS) {
            // 2-1. 활성 사용자로 등록
            waitListPort.incrementActiveUserCount(userId)
            return true
        }
        // 3. 임계값 초과
        return false
    }

    override fun addToQueue(userId: String): Long {
        // 1. 이미 활성 유저이거나, 이미 대기열에 있다면 순번만 반환
        if (waitListPort.isUserActive(userId)) return 0
        val rank = waitListPort.getWaitingRank(userId)
        if (rank != null) return rank + 1 // ZRANK는 0-based
        // 2. 대기열에 신규 추가
        return waitListPort.addToWaitingQueue(userId) + 1
    }
}