package event.oms.application.port.`in`.member

interface LoginUseCase {
    fun login(command: LoginCommand): TokenInfo
}