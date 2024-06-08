package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.repository.UserRepository
import kz.danekerscode.coassembleapi.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

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

    override fun save(user: User): Mono<Void> {
        return userRepository.save(user).then()
    }
}