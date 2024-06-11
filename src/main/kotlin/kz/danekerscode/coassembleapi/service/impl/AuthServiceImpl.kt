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
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.InetSocketAddress

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

    private var log = LoggerFactory.getLogger(this::class.java)

    override fun login(loginRequest: LoginRequest, exchange: ServerWebExchange): Mono<UserDto> =
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.email,
                loginRequest.password
            )
        )
//            .filter { todo fix and add this feature
//                val currentUser = it.principal as CoAssembleUserDetails
//                val user = currentUser.user
//
//                val lastLoginAddress: InetSocketAddress? = user.lastLoginAddress
//                if (lastLoginAddress == null) {
//                    it.isAuthenticated
//                }
//
//                val remoteAddress = exchange.request.remoteAddress
//
//                if (lastLoginAddress != remoteAddress) {
//                    return Mono.error(
//                        AuthProcessingException(
//                            "Last login address is different from current. Will send verification email.",
//                            HttpStatus.UNAUTHORIZED
//                        )
//
//                    )
//                }
//
//                it.isAuthenticated
//            }

            .flatMap { auth ->
                val securityContext: SecurityContext = SecurityContextImpl(auth)
                securityContextRepository
                    .save(exchange, securityContext)
                    .then(userService.me(loginRequest.email))
            }
            .onErrorResume {
                log.error("Login failed: ${it.message}", it)
                Mono.error(
                    AuthProcessingException(
                        "Login failed: ${it.message}",
                        HttpStatus.UNAUTHORIZED
                    )
                )
            }

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
        userService.createUser(registerRequest, registerRequest.password)
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

    override fun verifyEmail(token: String, email: String): Mono<UserDto> {
        return verificationTokenService.findByValueAndUserEmail(token, email, VerificationTokenType.MAIL_VERIFICATION)
            .switchIfEmpty(Mono.error(AuthProcessingException("Invalid verification token", HttpStatus.BAD_REQUEST)))
            .filter { !it.isExpired() }
            .switchIfEmpty(Mono.error(AuthProcessingException("Verification token expired", HttpStatus.BAD_REQUEST)))
            .flatMap { verificationToken ->
                verificationTokenService.deleteById(verificationToken.id!!)
                    .then(userService.verifyUserEmail(email))
                    .then(sendGreetingEmail(verificationToken.userEmail))
                    .then(userService.me(email))
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
            else -> throw AuthProcessingException("Invalid principal type", HttpStatus.INTERNAL_SERVER_ERROR)
        }


    private fun sendPasswordChangedEmail(email: String): Mono<Void> =
        mailService.sendMailMessage(
            SendMailMessageArgs(
                receiver = email,
                type = MailMessageType.PASSWORD_CHANGED,
                data = mapOf(
                    "receiverEmail" to email,
                    "loginPageLink" to "${coAssembleProperties.mailLinkPrefix}/login"
                )
            )
        )

    private fun constructMailVerificationLink(verificationToken: String, email: String): String =
        "${coAssembleProperties.mailLinkPrefix}/verify-email/$email?token=$verificationToken"

}

