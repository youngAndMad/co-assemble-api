package kz.danekerscode.coassembleapi.service.impl

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
import kz.danekerscode.coassembleapi.service.VerificationTokenService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Service
class AuthServiceImpl(
    private val mailService: MailService,
    private val authenticationManager: ReactiveAuthenticationManager,
    private val securityContextRepository: ServerSecurityContextRepository,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val coAssembleProperties: CoAssembleProperties,
    private val verificationTokenService: VerificationTokenService
) : AuthService {

    override fun login(loginRequest: LoginRequest, exchange: ServerWebExchange): Mono<Void> {
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
            loginRequest.email,
            loginRequest.password
        )

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken)
            .flatMap { authentication ->
                val securityContext = SecurityContextHolder.createEmptyContext()
                securityContext.authentication = authentication
                securityContextRepository.save(exchange, securityContext)
            }
            .then()
            .onErrorResume { error ->
                Mono.error(AuthProcessingException("Login failed: ${error.message}", HttpStatus.UNAUTHORIZED))
            }
    }

    override fun register(registerRequest: RegistrationRequest): Mono<Void> {
        return userService.existsByEmailAndProvider(registerRequest.email, AuthType.MANUAL)
            .flatMap { exists ->
                if (exists) {
                    Mono.error(
                        AuthProcessingException(
                            "User with email ${registerRequest.email} already exists",
                            HttpStatus.BAD_REQUEST
                        )
                    )
                } else {
                    userService.createUser(registerRequest, passwordEncoder.encode(registerRequest.password))
                }
            }
            .flatMap { user ->
                verificationTokenService.generateForUser(registerRequest.email)
                    .flatMap { token ->
                        val sendMailMessageArgs = SendMailMessageArgs(
                            registerRequest.email,
                            MailMessageType.MAIL_CONFIRMATION,
                            mapOf(
                                "receiverEmail" to registerRequest.email,
                                "verificationTokenTtl" to coAssembleProperties.verificationTokenTtl.toMinutes()
                                    .toString(),
                                "link" to constructMailVerificationLink(token.value, user.email)
                            )
                        )
                        mailService.sendMailMessage(sendMailMessageArgs)
                    }
            }
            .then()
    }

    private fun constructMailVerificationLink(
        verificationToken: String,
        email: String
    ): String =
        "${coAssembleProperties.domain}/api/v1/auth/confirm-email/${email}?token=$verificationToken"
}
