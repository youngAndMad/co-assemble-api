package kz.danekerscode.coassembleapi.controller

import io.swagger.v3.oas.annotations.Operation
import kz.danekerscode.coassembleapi.model.dto.auth.ForgotPasswordConfirmation
import kz.danekerscode.coassembleapi.model.dto.auth.LoginRequest
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
import kz.danekerscode.coassembleapi.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest, exchange: ServerWebExchange) =
        authService.login(loginRequest, exchange)

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    fun register(@RequestBody registerRequest: RegistrationRequest) = authService.register(registerRequest)

        @GetMapping("/verify-email")
    @Operation(summary = "Verify email")
    fun verifyEmail(
        @RequestParam token: String,
        @RequestParam email: String
    ) = authService.verifyEmail(token, email)

    @Operation(summary = "Forgot password request to send email with reset password link")
    @PostMapping("/forgot-password/request/{email}")
    fun forgotPasswordRequest(
        @PathVariable email: String
    ) = authService.forgotPasswordRequest(email)

    @Operation(summary = "Forgot password confirm")
    @PostMapping("/forgot-password/confirm/{email}")
    fun forgotPasswordConfirm(
        @RequestBody forgotPasswordConfirmation: ForgotPasswordConfirmation
    ) = authService.forgotPasswordConfirm(forgotPasswordConfirmation)

    @GetMapping("/me")
    @Operation(summary = "Get current user")
    fun me(authentication: Authentication): Mono<UserDto> = authService.me(authentication)
}