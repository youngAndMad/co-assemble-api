package kz.danekerscode.coassembleapi.security

import kz.danekerscode.coassembleapi.model.exception.AuthProcessingException
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CoAssembleAuthenticationProvider(
    private val passwordEncoder: PasswordEncoder,
    private val coAssembleUserDetailsService: ReactiveUserDetailsService
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        return Mono.justOrEmpty(authentication)
            .filter { it is UsernamePasswordAuthenticationToken }
            .cast(UsernamePasswordAuthenticationToken::class.java)
            .flatMap { auth ->
                val username = auth.name
                val password = auth.credentials.toString()

                coAssembleUserDetailsService.findByUsername(username)
                    .switchIfEmpty(Mono.error(AuthProcessingException("Invalid Credentials", HttpStatus.UNAUTHORIZED)))
                    .flatMap { userDetails ->
                        if (passwordEncoder.matches(password, userDetails.password))
                            Mono.just(
                                UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    password,
                                    userDetails.authorities
                                )
                            )
                        else
                            Mono.error(AuthProcessingException("Invalid Credentials", HttpStatus.UNAUTHORIZED))
                    }
            }
    }
}
