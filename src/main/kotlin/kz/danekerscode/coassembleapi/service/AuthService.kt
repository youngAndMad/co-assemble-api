package kz.danekerscode.coassembleapi.service

import kz.danekerscode.coassembleapi.model.dto.auth.LoginRequest
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import reactor.core.publisher.Mono

/**
 *  Service for authentication
* */
interface AuthService {

    /**
     * Login user
    * */
    fun login(loginRequest: LoginRequest): Mono<Void>

    fun register(registerRequest: RegistrationRequest): Mono<Void>
}