package kz.danekerscode.coassembleapi.service

import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import reactor.core.publisher.Mono

interface UserService {

    fun existsByEmailAndProvider(
        username: String,
        provider: AuthType
    ): Mono<Boolean>

    fun save(user: User): Mono<User>

    fun createUser(
        registerRequest: RegistrationRequest,
        password: String
    ): Mono<User>

    fun verifyUserEmail(email: String): Mono<Void>

    fun findByEmail(email: String): Mono<User>

    fun updatePassword(email: String, updatedPassword: String): Mono<Void>

    fun me(email: String): Mono<UserDto>

    fun createAdmin(email: String, password: String): Mono<Void>

}