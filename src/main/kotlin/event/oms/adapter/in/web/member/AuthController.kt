package event.oms.adapter.`in`.web.member

import event.oms.adapter.`in`.web.common.BaseResponse
import event.oms.adapter.`in`.web.common.BaseResponse.Companion.toResponseEntity
import event.oms.adapter.`in`.web.member.request.LoginRequest
import event.oms.adapter.`in`.web.member.request.RefreshRequest
import event.oms.adapter.`in`.web.member.request.SignUpRequest
import event.oms.adapter.`in`.web.member.response.MemberResponse
import event.oms.adapter.`in`.web.member.response.TokenResponse
import event.oms.application.port.`in`.member.LoginUseCase
import event.oms.application.port.`in`.member.LogoutUseCase
import event.oms.application.port.`in`.member.RefreshTokenUseCase
import event.oms.application.port.`in`.member.SignUpUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val signUpUseCase      : SignUpUseCase,
    private val loginUseCase       : LoginUseCase,
    private val logoutUseCase      : LogoutUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
): AuthSpec {
    @PostMapping("/signup")
    override fun signUp(@Valid @RequestBody request: SignUpRequest): ResponseEntity<BaseResponse<MemberResponse>> {
        val command = request.toCommand()
        val newMember = signUpUseCase.signUp(command)
        return BaseResponse.created(MemberResponse.from(newMember)).toResponseEntity()
    }

    @PostMapping("/login")
    override fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<BaseResponse<TokenResponse>> {
        val command = request.toCommand()
        val tokenInfo = loginUseCase.login(command)
        return BaseResponse.ok(TokenResponse.from(tokenInfo)).toResponseEntity()
    }

    @PostMapping("/logout")
    override fun logout(authentication: Authentication): ResponseEntity<BaseResponse<String>> {
        // 인증된 사용자의 username으로 로그아웃 처리
        logoutUseCase.logout(authentication.name)
        return BaseResponse.ok("로그아웃 되었습니다.").toResponseEntity()
    }

    @PostMapping("/refresh")
    override fun refresh(@Valid @RequestBody request: RefreshRequest): ResponseEntity<BaseResponse<TokenResponse>> {
        val command = request.toCommand()
        val newTokenInfo = refreshTokenUseCase.refreshTokens(command)
        return BaseResponse.ok(TokenResponse.from(newTokenInfo)).toResponseEntity()
    }
}