package event.oms.adapter.`in`.web.member

import event.oms.adapter.`in`.web.common.BaseResponse
import event.oms.adapter.`in`.web.member.request.LoginRequest
import event.oms.adapter.`in`.web.member.request.RefreshRequest
import event.oms.adapter.`in`.web.member.request.SignUpRequest
import event.oms.adapter.`in`.web.member.response.MemberResponse
import event.oms.adapter.`in`.web.member.response.TokenResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication

@Tag(name = "인증 API", description = "회원가입, 로그인, 로그아웃, 재발급")
interface AuthSpec {
    @Operation(summary = "", description = "")
    fun signUp(request: SignUpRequest): ResponseEntity<BaseResponse<MemberResponse>>

    @Operation(summary = "", description = "")
    fun login(request: LoginRequest): ResponseEntity<BaseResponse<TokenResponse>>

    @Operation(summary = "", description = "")
    fun logout(authentication: Authentication): ResponseEntity<BaseResponse<String>>

    @Operation(summary = "", description = "")
    fun refresh(request: RefreshRequest): ResponseEntity<BaseResponse<TokenResponse>>
}