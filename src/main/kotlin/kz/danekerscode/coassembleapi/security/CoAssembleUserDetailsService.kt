package kz.danekerscode.coassembleapi.security

import kz.danekerscode.coassembleapi.model.exception.AuthProcessingException
import kz.danekerscode.coassembleapi.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CoAssembleUserDetailsService(
    val userService: UserService,
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String?): Mono<UserDetails> =
        Mono.justOrEmpty(username)
            .switchIfEmpty(Mono.error(AuthProcessingException("Username is required.", HttpStatus.UNAUTHORIZED)))
            .flatMap { userService.findByEmail(it) }
            .switchIfEmpty(Mono.error(AuthProcessingException("Invalid credentials", HttpStatus.UNAUTHORIZED)))
            .filter { it.emailVerified }
            .switchIfEmpty(Mono.error(AuthProcessingException("Email not verified", HttpStatus.UNAUTHORIZED)))
            .map { CoAssembleUserDetails(it) }

}