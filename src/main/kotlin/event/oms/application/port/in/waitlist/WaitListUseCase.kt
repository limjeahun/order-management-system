package event.oms.application.port.`in`.waitlist

interface WaitListUseCase {
    fun isAllowed(userId: String) : Boolean
    fun addToQueue(userId: String): Long
}