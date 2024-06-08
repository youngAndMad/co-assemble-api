package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.config.CoAssembleConstants
import kz.danekerscode.coassembleapi.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.model.dto.auth.LoginRequest
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.model.enums.MailMessageType
import kz.danekerscode.coassembleapi.model.exception.AuthProcessingException
import kz.danekerscode.coassembleapi.model.payload.SendMailMessageArgs
import kz.danekerscode.coassembleapi.service.AuthService
import kz.danekerscode.coassembleapi.service.MailService
import kz.danekerscode.coassembleapi.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.session.ReactiveSessionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthServiceImpl(
    private val mailService: MailService,
    private val authenticationManager: ReactiveAuthenticationManager,
    private val sessionRepository: ReactiveSessionRepository<*>,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val coAssembleProperties: CoAssembleProperties
) : AuthService {

    override fun login(loginRequest: LoginRequest): Mono<Void> {
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
            loginRequest.email,
            loginRequest.password
        )

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken)
            .flatMap { authentication ->
                val session = sessionRepository.createSession() // Create a new session manually
                val securityContext = SecurityContextHolder.createEmptyContext()
                securityContext.authentication = authentication
                session.flatMap<Void> {
                    it.setAttribute(
                        CoAssembleConstants.SPRING_SECURITY_CONTEXT,
                        securityContext
                    )
                    Mono.empty()
                }
            }
            .then()
            .onErrorResume { error ->
                Mono.error(AuthProcessingException("Login failed: ${error.message}", HttpStatus.UNAUTHORIZED))
            }
    }

    override fun register(registerRequest: RegistrationRequest): Mono<Void> {
        return userService.existsByEmailAndProvider(registerRequest.email, AuthType.MANUAL)
            .flatMap { exists ->
                if (exists)
                    Mono.error(
                        AuthProcessingException(
                            "User with email ${registerRequest.email} already exists",
                            HttpStatus.BAD_REQUEST
                        )
                    )
                else
                    userService.createUser(registerRequest, passwordEncoder.encode(registerRequest.password))
                        .then(Mono.defer {
                            val sendMailMessageArgs = SendMailMessageArgs(
                                registerRequest.email,
                                MailMessageType.MAIL_CONFIRMATION,
                                mapOf(
                                    "receiverEmail" to registerRequest.email,
                                    "verificationTokenTtl" to coAssembleProperties.verificationTokenTtl.toMinutes().toString(),
                                    "link" to ""
                                )
                            )
                            mailService.sendMailMessage(sendMailMessageArgs)
                                .then()
                        })
                        .then()
            }
    }


    private fun constructMailVerificationLink(verificationToken: String): String =
        "${coAssembleProperties.domain}/auth/verify?token=$verificationToken"
}
