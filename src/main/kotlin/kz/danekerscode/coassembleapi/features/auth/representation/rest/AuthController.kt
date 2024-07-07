package kz.danekerscode.coassembleapi.features.auth.representation.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.Email
import kz.danekerscode.coassembleapi.features.user.data.mapper.UserMapper
import kz.danekerscode.coassembleapi.features.auth.representation.dto.ForgotPasswordConfirmation
import kz.danekerscode.coassembleapi.features.auth.representation.dto.LoginRequest
import kz.danekerscode.coassembleapi.features.auth.representation.dto.RegistrationRequest
import kz.danekerscode.coassembleapi.features.user.representation.dto.UserDto
import kz.danekerscode.coassembleapi.features.auth.data.enums.VerificationTokenType
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.auth.domain.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val userMapper: UserMapper
) {

    @PostMapping("/login")
    suspend fun login(
        @RequestBody @Validated loginRequest: LoginRequest,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) = authService.login(loginRequest, request, response)

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    suspend fun register(@RequestBody registerRequest: RegistrationRequest) = authService.register(registerRequest)

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email")
    suspend fun verifyEmail(
        @RequestParam token: String,
        @RequestParam email: String
    ) = authService.verifyEmail(token, email)

    @Operation(summary = "Resend email")
    @PostMapping("/resend-email/{email}")
    suspend fun resendEmail(
        @PathVariable @Email email: String,
        @RequestParam type: VerificationTokenType
    ) = authService.resendEmail(email, type)

    @Operation(summary = "Forgot password request to send email with reset password link")
    @PostMapping("/forgot-password/request/{email}")
    suspend fun forgotPasswordRequest(
        @PathVariable @Email email: String
    ) = authService.forgotPasswordRequest(email)

    @Operation(summary = "Forgot password confirm")
    @PostMapping("/forgot-password/confirm")
    suspend fun forgotPasswordConfirm(
        @RequestBody forgotPasswordConfirmation: ForgotPasswordConfirmation
    ) = authService.forgotPasswordConfirm(forgotPasswordConfirmation)

    @GetMapping("/me")
    @Operation(summary = "Get current user")
    suspend fun me(@AuthenticationPrincipal currentUser: CoAssembleUserDetails):
            UserDto = userMapper.toUserDto(currentUser.user)
}