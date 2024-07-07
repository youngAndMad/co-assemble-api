package kz.danekerscode.coassembleapi.features.auth.domain.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kz.danekerscode.coassembleapi.features.auth.representation.dto.ForgotPasswordConfirmation
import kz.danekerscode.coassembleapi.features.auth.representation.dto.LoginRequest
import kz.danekerscode.coassembleapi.features.auth.representation.dto.RegistrationRequest
import kz.danekerscode.coassembleapi.features.user.representation.dto.UserDto
import kz.danekerscode.coassembleapi.features.auth.data.enums.VerificationTokenType
import org.springframework.security.core.Authentication

/**
 *  Service for authentication
 * */
interface AuthService {

    /**
     * Login user
     * */
    suspend fun login(loginRequest: LoginRequest, request: HttpServletRequest, response: HttpServletResponse): UserDto

    suspend fun register(registerRequest: RegistrationRequest)

    suspend fun resendEmail(email: String, type: VerificationTokenType)

    suspend fun verifyEmail(token: String, email: String): UserDto

    suspend fun forgotPasswordRequest(email: String)

    suspend fun forgotPasswordConfirm(forgotPasswordConfirmation: ForgotPasswordConfirmation)

    suspend fun me(auth: Authentication): UserDto
}