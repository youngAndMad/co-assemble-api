package kz.danekerscode.coassembleapi.service

import kz.danekerscode.coassembleapi.model.dto.auth.ForgotPasswordConfirmation
import kz.danekerscode.coassembleapi.model.dto.auth.LoginRequest
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
import org.springframework.security.core.Authentication
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 *  Service for authentication
 * */
interface AuthService {

    /**
     * Login user
     * */
    fun login(loginRequest: LoginRequest, exchange: ServerWebExchange): Mono<UserDto>

    fun register(registerRequest: RegistrationRequest): Mono<Void>

    fun verifyEmail(token: String, email: String): Mono<UserDto>

    fun forgotPasswordRequest(email: String): Mono<Void>

    fun forgotPasswordConfirm(forgotPasswordConfirmation: ForgotPasswordConfirmation): Mono<Void>

    fun me(auth: Authentication): Mono<UserDto>
}