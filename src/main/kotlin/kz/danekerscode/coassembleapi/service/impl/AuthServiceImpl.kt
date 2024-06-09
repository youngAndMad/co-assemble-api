package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.model.dto.auth.ForgotPasswordConfirmation
import kz.danekerscode.coassembleapi.model.dto.auth.LoginRequest
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.model.enums.MailMessageType
import kz.danekerscode.coassembleapi.model.enums.VerificationTokenType
import kz.danekerscode.coassembleapi.model.exception.AuthProcessingException
import kz.danekerscode.coassembleapi.model.payload.SendMailMessageArgs
import kz.danekerscode.coassembleapi.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.service.AuthService
import kz.danekerscode.coassembleapi.service.MailService
import kz.danekerscode.coassembleapi.service.UserService
import kz.danekerscode.coassembleapi.service.VerificationTokenService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
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

    override fun login(loginRequest: LoginRequest, exchange: ServerWebExchange): Mono<Void> =
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.email,
                loginRequest.password
            )
        )
            .flatMap { auth ->
                val securityContext = SecurityContextHolder.createEmptyContext().apply { authentication = auth }
                securityContextRepository.save(exchange, securityContext)
            }
            .onErrorResume {
                Mono.error(
                    AuthProcessingException(
                        "Login failed: ${it.message}",
                        HttpStatus.UNAUTHORIZED
                    )
                )
            }
            .log()

    override fun register(registerRequest: RegistrationRequest): Mono<Void> =
        userService.existsByEmailAndProvider(registerRequest.email, AuthType.MANUAL)
            .flatMap { exists ->
                if (exists) {
                    Mono.error(
                        AuthProcessingException(
                            "User with email ${registerRequest.email} already exists",
                            HttpStatus.BAD_REQUEST
                        )
                    )
                } else {
                    createUserAndSendVerification(registerRequest)
                }
            }
            .then()

    private fun createUserAndSendVerification(registerRequest: RegistrationRequest): Mono<Void> =
        userService.createUser(registerRequest, passwordEncoder.encode(registerRequest.password))
            .flatMap { user ->
                verificationTokenService.generateForUser(registerRequest.email, VerificationTokenType.MAIL_VERIFICATION)
                    .flatMap { token ->
                        val args = SendMailMessageArgs(
                            registerRequest.email,
                            MailMessageType.MAIL_CONFIRMATION,
                            mapOf(
                                "receiverEmail" to registerRequest.email,
                                "verificationTokenTtl" to coAssembleProperties.verificationTokenTtl.toMinutes()
                                    .toString(),
                                "link" to constructMailVerificationLink(token.value, user.email)
                            )
                        )
                        mailService.sendMailMessage(args)
                    }
            }

    override fun verifyEmail(token: String, email: String): Mono<Void> {
        return verificationTokenService.findByValueAndUserEmail(token, email, VerificationTokenType.MAIL_VERIFICATION)
            .switchIfEmpty(Mono.error(AuthProcessingException("Invalid verification token", HttpStatus.BAD_REQUEST)))
            .flatMap { verificationToken ->
                verificationTokenService.deleteById(verificationToken.id!!)
                    .then(userService.verifyUserEmail(email))
                    .then(sendGreetingEmail(verificationToken.userEmail))
            }
    }

    private fun sendGreetingEmail(email: String): Mono<Void> =
        mailService.sendMailMessage(
            SendMailMessageArgs(
                receiver = email,
                type = MailMessageType.GREETING,
                data = mapOf("receiverEmail" to email, "domain" to coAssembleProperties.domain)
            )
        )

    override fun forgotPasswordRequest(
        email: String
    ): Mono<Void> = userService.findByEmail(email)
        .filter { it.provider == AuthType.MANUAL }
        .switchIfEmpty(
            Mono.error(
                AuthProcessingException(
                    "User with email $email not found",
                    HttpStatus.NOT_FOUND
                )
            )
        )
        .flatMap {
            verificationTokenService.generateForUser(email, VerificationTokenType.FORGOT_PASSWORD)
                .flatMap { token ->
                    mailService.sendMailMessage(
                        SendMailMessageArgs(
                            email,
                            MailMessageType.FORGOT_PASSWORD,
                            mapOf(
                                "receiverEmail" to email,
                                "verificationTokenTtl" to coAssembleProperties.verificationTokenTtl.toMinutes()
                                    .toString(),
                                "link" to "${coAssembleProperties.mailLinkPrefix}/forgot-password/confirm/$email?token=${token.value}"
                            )
                        )
                    )
                }
        }
        .then()

    override fun forgotPasswordConfirm(forgotPasswordConfirmation: ForgotPasswordConfirmation): Mono<Void> =
        verificationTokenService.findByValueAndUserEmail(
            forgotPasswordConfirmation.token, forgotPasswordConfirmation.email, VerificationTokenType.FORGOT_PASSWORD
        )
            .switchIfEmpty(Mono.error(AuthProcessingException("Invalid verification token", HttpStatus.BAD_REQUEST)))
            .flatMap { verificationToken ->
                verificationTokenService
                    .deleteById(verificationToken.id!!)
                    .then(
                        userService.updatePassword(
                            forgotPasswordConfirmation.email,
                            passwordEncoder.encode(forgotPasswordConfirmation.password)
                        )
                    )
                    .then(sendPasswordChangedEmail(verificationToken.userEmail))
            }

    override fun me(auth: Authentication): Mono<UserDto> =
        when (val principal = auth.principal) {
            is CoAssembleUserDetails -> userService.me(principal.username)
//            is OAuth2AuthenticationToken -> userService.me(principal.principal/)/
            else -> throw AuthProcessingException("Invalid principal type", HttpStatus.INTERNAL_SERVER_ERROR)
        }


    private fun sendPasswordChangedEmail(email: String): Mono<Void> {
        val args = SendMailMessageArgs(
            receiver = email,
            type = MailMessageType.PASSWORD_CHANGED,
            data = mapOf("receiverEmail" to email, "loginPageLink" to "${coAssembleProperties.mailLinkPrefix}/login")
        )
        return mailService.sendMailMessage(args)
    }

    private fun constructMailVerificationLink(verificationToken: String, email: String): String =
        "${coAssembleProperties.mailLinkPrefix}/verify-email/$email?token=$verificationToken"

}

