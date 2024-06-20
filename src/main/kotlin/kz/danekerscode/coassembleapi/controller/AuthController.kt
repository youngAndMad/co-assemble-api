package kz.danekerscode.coassembleapi.controller

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.constraints.Email
import kz.danekerscode.coassembleapi.core.mapper.UserMapper
import kz.danekerscode.coassembleapi.model.dto.auth.ForgotPasswordConfirmation
import kz.danekerscode.coassembleapi.model.dto.auth.LoginRequest
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
import kz.danekerscode.coassembleapi.model.enums.VerificationTokenType
import kz.danekerscode.coassembleapi.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val userMapper: UserMapper
) {

    @PostMapping("/login")
    fun login(
        @RequestBody @Validated loginRequest: LoginRequest,
        exchange: ServerWebExchange
    ) = authService.login(loginRequest, exchange)

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    fun register(@RequestBody registerRequest: RegistrationRequest) = authService.register(registerRequest)

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email")
    fun verifyEmail(
        @RequestParam token: String,
        @RequestParam email: String,
        exchange: ServerWebExchange
    ) = authService.verifyEmail(token, email)

    @Operation(summary = "Resend email")
    @PostMapping("/resend-email/{email}")
    fun resendEmail(
        @PathVariable @Email email: String,
        @RequestParam type: VerificationTokenType
    ) = authService.resendEmail(email, type)

    @Operation(summary = "Forgot password request to send email with reset password link")
    @PostMapping("/forgot-password/request/{email}")
    fun forgotPasswordRequest(
        @PathVariable @Email email: String
    ) = authService.forgotPasswordRequest(email)

    @Operation(summary = "Forgot password confirm")
    @PostMapping("/forgot-password/confirm")
    fun forgotPasswordConfirm(
        @RequestBody forgotPasswordConfirmation: ForgotPasswordConfirmation
    ) = authService.forgotPasswordConfirm(forgotPasswordConfirmation)

    @GetMapping("/me")
    @Operation(summary = "Get current user")
    fun me(@AuthenticationPrincipal currentUser: CoAssembleUserDetails):
            Mono<UserDto> = Mono.just(userMapper.toUserDto(currentUser.user))
}