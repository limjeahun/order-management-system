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
    @Operation(summary = "회원 가입", description = "새로운 회원 정보를 시스템에 등록합니다.")
    fun signUp(request: SignUpRequest): ResponseEntity<BaseResponse<MemberResponse>>

    @Operation(summary = "로그인", description = "회원 인증 처리를 합니다.")
    fun login(request: LoginRequest): ResponseEntity<BaseResponse<TokenResponse>>

    @Operation(summary = "로그아웃", description = "회원 로그아웃 처리를 합니다.")
    fun logout(authentication: Authentication): ResponseEntity<BaseResponse<String>>

    @Operation(summary = "로그인 상태 재 인증", description = "로그인 상태 재 인증처리합니다.")
    fun refresh(request: RefreshRequest): ResponseEntity<BaseResponse<TokenResponse>>
}