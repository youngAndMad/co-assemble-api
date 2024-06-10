package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.core.mapper.UserMapper
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.repository.UserRepository
import kz.danekerscode.coassembleapi.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.nio.file.attribute.UserPrincipalNotFoundException

@Service
class UserServiceImpl(
    private var userRepository: UserRepository,
    private var userMapper: UserMapper,
    private var passwordEncoder: PasswordEncoder
) : UserService {

    private var log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun existsByEmailAndProvider(
        username: String, provider: AuthType
    ): Mono<Boolean> {
        log.debug("Checking if user exists by username: {} and provider: {}", username, provider)
        return userRepository.existsByEmailAndProvider(username, provider)
    }

    override fun save(user: User): Mono<User> = userRepository.save(user)

    override fun createUser(
        registerRequest: RegistrationRequest,
        password: String
    ): Mono<User> {
        val user = User()
        user.email = registerRequest.email
        user.username = registerRequest.username
        user.password = passwordEncoder.encode(password)

        return this.save(user)
    }

    override fun verifyUserEmail(email: String): Mono<Void> =
        userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(UserPrincipalNotFoundException("User not found for email: $email")))
            .flatMap { user ->
                if (user.emailVerified) {
                    // Email already verified, no need to update
                    Mono.empty()
                } else {
                    // Update email verification status
                    user.emailVerified = true
                    userRepository.save(user).then()
                }
            }

    override fun findByEmail(email: String): Mono<User> = userRepository.findByEmail(email)

    override fun updatePassword(email: String, updatedPassword: String): Mono<Void> =
        userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(UserPrincipalNotFoundException("User not found for email: $email")))
            .flatMap { user ->
                user.password = updatedPassword
                userRepository.save(user).then()
            }

    override fun me(email: String): Mono<UserDto> = userRepository
        .findByEmail(email)
        .map { userMapper.toMeResponse(it) }

    override fun createAdmin(email: String, password: String): Mono<Void> =
        existsByEmailAndProvider(email, AuthType.MANUAL)
            .flatMap { exists ->
                if (exists) {
                    log.info("Admin with email {} already exists", email)
                    Mono.empty()
                } else {
                    val user = userMapper.toAdmin(email, passwordEncoder.encode(password))
                    userRepository.save(user).flatMap {
                        log.info("Admin with email {} created successfully", email)
                        Mono.empty()
                    }
                }

            }
}