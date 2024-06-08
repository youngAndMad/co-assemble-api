package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.repository.UserRepository
import kz.danekerscode.coassembleapi.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.nio.file.attribute.UserPrincipalNotFoundException

@Service
class UserServiceImpl(
    private var userRepository: UserRepository
) : UserService {

    private var log: Logger = LoggerFactory.getLogger(this.javaClass::class.java)

    override fun existsByEmailAndProvider(
        username: String, provider: AuthType
    ): Mono<Boolean> {
        log.debug("Checking if user exists by username: {} and provider: {}", username, provider)
        return userRepository.existsByEmailAndProvider(username, provider)
    }

    override fun save(user: User): Mono<User> {
        return userRepository.save(user)
    }

    override fun createUser(
        registerRequest: RegistrationRequest,
        hashPassword: String
    ): Mono<User> {
        val user = User()
        user.email = registerRequest.email
        user.username = registerRequest.username
        user.password = hashPassword

        return this.save(user)
    }

    override fun verifyUserEmail(email: String): Mono<Void> {
        return userRepository.findByEmail(email)
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
    }
}