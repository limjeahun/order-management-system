package event.oms.application.port.out.waitlist

interface WaitListPort {
    fun getActiveUserCount(): Long
    fun incrementActiveUserCount(userId: String): Long
    fun addToWaitingQueue(userId: String): Long
    fun getWaitingRank(userId: String): Long?
    fun isUserActive(userId: String): Boolean
}