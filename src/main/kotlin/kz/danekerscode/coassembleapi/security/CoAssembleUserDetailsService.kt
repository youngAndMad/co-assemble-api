package kz.danekerscode.coassembleapi.security

import kz.danekerscode.coassembleapi.model.exception.AuthProcessingException
import kz.danekerscode.coassembleapi.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CoAssembleUserDetailsService(
    val userRepository: UserRepository,
) : ReactiveUserDetailsService {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun findByUsername(username: String?): Mono<UserDetails> {
        log.debug("Loading user by username: $username")

        return Mono.justOrEmpty(username)
            .switchIfEmpty(Mono.error(AuthProcessingException("Username is required.", HttpStatus.BAD_REQUEST)))
            .flatMap { userRepository.findByEmail(it) }
            .filter { it.emailVerified }
            .switchIfEmpty(Mono.error(AuthProcessingException("Email not verified", HttpStatus.BAD_REQUEST)))
            .map { CoAssembleUserDetails(it) }
    }

}