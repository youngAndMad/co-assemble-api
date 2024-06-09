package kz.danekerscode.coassembleapi.controller

import kz.danekerscode.coassembleapi.model.dto.auth.LoginRequest
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

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
    fun register(@RequestBody registerRequest: RegistrationRequest) = authService.register(registerRequest)

    @GetMapping("/verify-email/{email}")
    fun verifyEmail(
        @RequestParam token: String,
        @PathVariable email: String
    ) = authService.verifyEmail(token, email)

    @PostMapping("/forgot-password/request/{email}")
    fun forgotPasswordRequest(
        @PathVariable email: String
    ) = authService.forgotPasswordRequest(email)

    @PostMapping("/forgot-password/confirm/{email}")
    fun forgotPasswordConfirm(
        @RequestParam token: String,
        @PathVariable email: String
    ) = authService.forgotPasswordConfirm(token, email)
}