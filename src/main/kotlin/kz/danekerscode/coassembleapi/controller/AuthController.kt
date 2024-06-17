package kz.danekerscode.coassembleapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.constraints.Email
import kz.danekerscode.coassembleapi.config.CoAssembleConstants
import kz.danekerscode.coassembleapi.model.dto.auth.ForgotPasswordConfirmation
import kz.danekerscode.coassembleapi.model.dto.auth.LoginRequest
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
import kz.danekerscode.coassembleapi.model.enums.VerificationTokenType
import kz.danekerscode.coassembleapi.service.AuthService
import kz.danekerscode.coassembleapi.utils.CookieUtils
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val objectMapper: ObjectMapper
) {

    @PostMapping("/login")
    fun login(
        @RequestBody @Validated loginRequest: LoginRequest,
        exchange: ServerWebExchange
    ) = authService.login(loginRequest, exchange)
        .flatMap<Void> {
            CookieUtils.addCookie(
                exchange.response,
                CoAssembleConstants.USER,
                objectMapper.writeValueAsString(it),
                3600
            )
            Mono.empty()
        }

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
        .flatMap<Void> {
            CookieUtils.addCookie(
                exchange.response,
                CoAssembleConstants.USER,
                objectMapper.writeValueAsString(it),
                3600
            )
            Mono.empty()
        }


    @Operation(summary = "Resend email")
    @PostMapping("/resend-email/{email}")
    fun resendEmail(
        @PathVariable @Email email: String,
        @RequestParam type: VerificationTokenType
    ) = authService.resendEmail(email, type)

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