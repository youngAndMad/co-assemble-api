package kz.danekerscode.coassembleapi.service

import kz.danekerscode.coassembleapi.model.dto.auth.ForgotPasswordConfirmation
import kz.danekerscode.coassembleapi.model.dto.auth.LoginRequest
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 *  Service for authentication
 * */
interface AuthService {

    /**
     * Login user
     * */
    fun login(loginRequest: LoginRequest, exchange: ServerWebExchange): Mono<Void>

    fun register(registerRequest: RegistrationRequest): Mono<Void>

    fun verifyEmail(token: String, email: String): Mono<Void>

    fun forgotPasswordRequest(email: String): Mono<Void>

    fun forgotPasswordConfirm(forgotPasswordConfirmation: ForgotPasswordConfirmation): Mono<Void>
}