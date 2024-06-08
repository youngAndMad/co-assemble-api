package kz.danekerscode.coassembleapi.service

import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import reactor.core.publisher.Mono

interface UserService {

    fun existsByEmailAndProvider(
        username: String,
        provider: AuthType
    ): Mono<Boolean>

    fun save(user: User): Mono<Void>

    fun createUser(
        registerRequest: RegistrationRequest,
        hashPassword: String
    ): Mono<Void>

}