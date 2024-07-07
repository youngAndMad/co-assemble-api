package kz.danekerscode.coassembleapi.features.auth.domain.service.impl

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kz.danekerscode.coassembleapi.core.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.auth.data.enums.AuthType
import kz.danekerscode.coassembleapi.features.auth.data.enums.VerificationTokenType
import kz.danekerscode.coassembleapi.features.auth.domain.service.AuthService
import kz.danekerscode.coassembleapi.features.auth.domain.service.VerificationTokenService
import kz.danekerscode.coassembleapi.features.auth.representation.dto.ForgotPasswordConfirmation
import kz.danekerscode.coassembleapi.features.auth.representation.dto.LoginRequest
import kz.danekerscode.coassembleapi.features.auth.representation.dto.RegistrationRequest
import kz.danekerscode.coassembleapi.features.mail.data.enums.MailMessageType
import kz.danekerscode.coassembleapi.features.mail.representation.payload.SendMailMessageEvent
import kz.danekerscode.coassembleapi.features.user.domain.service.UserService
import kz.danekerscode.coassembleapi.features.user.representation.dto.UserDto
import kz.danekerscode.coassembleapi.model.exception.AuthProcessingException
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val securityContextRepository: SecurityContextRepository,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val coAssembleProperties: CoAssembleProperties,
    private val verificationTokenService: VerificationTokenService,
    private val eventBus: ApplicationEventPublisher
) : AuthService {

    private var log = LoggerFactory.getLogger(this::class.java)

    override suspend fun login(
        loginRequest: LoginRequest,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): UserDto {
        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.email,
                loginRequest.password
            )
        )

        val securityContext: SecurityContext = SecurityContextImpl(auth)
        log.info("Saving security context {}", auth.name)
        securityContextRepository.saveContext(securityContext, request, response)

        return userService.me(auth.name)
    }

    override suspend fun register(registerRequest: RegistrationRequest) {
        val existsByEmailAndProvider = userService.existsByEmailAndProvider(registerRequest.email, AuthType.MANUAL)
        if (existsByEmailAndProvider) {
            throw AuthProcessingException(
                "User with email ${registerRequest.email} already exists",
                HttpStatus.BAD_REQUEST
            )
        }
        log.info("Creating user with email {}", registerRequest.email)
        createUserAndSendVerification(registerRequest)
    }

    override suspend fun resendEmail(email: String, type: VerificationTokenType) {
        val existsByEmailAndProvider = userService.existsByEmailAndProvider(email, AuthType.MANUAL)
        if (existsByEmailAndProvider) {

            throw AuthProcessingException(
                "User with email $email not found",
                HttpStatus.NOT_FOUND
            )
        }
        verificationTokenService.revokeForUserByType(email, type)
        verificationTokenService.generateForUser(email, type).also {
            publishMailEvent(
                SendMailMessageEvent(
                    email,
                    type.mailMessageType,
                    mapOf(
                        "receiverEmail" to email,
                        "verificationTokenTtl" to coAssembleProperties.verificationTokenTtl.toMinutes()
                            .toString(),
                        "link" to constructMailVerificationLink(it.value, email)
                    )
                )
            )
        }
    }

    private suspend fun createUserAndSendVerification(registerRequest: RegistrationRequest) =
        userService.createUser(registerRequest, registerRequest.password)
            .also { user ->
                log.info("User with email {} created", user.email)
                verificationTokenService.generateForUser(
                    registerRequest.email,
                    VerificationTokenType.MAIL_VERIFICATION
                )
                    .also { token ->
                        log.info("Verification token generated for user {}", user.email)
                        publishMailEvent(
                            SendMailMessageEvent(
                                registerRequest.email,
                                MailMessageType.MAIL_CONFIRMATION,
                                mapOf(
                                    "receiverEmail" to registerRequest.email,
                                    "verificationTokenTtl" to coAssembleProperties.verificationTokenTtl.toMinutes()
                                        .toString(),
                                    "link" to constructMailVerificationLink(token.value, user.email)
                                )
                            )
                        )
                    }
            }

    override suspend fun verifyEmail(token: String, email: String): UserDto {
        val verificationToken = verificationTokenService.findByValueAndUserEmail(
            token,
            email,
            VerificationTokenType.MAIL_VERIFICATION
        ).also { it.checkValidation() }

        verificationTokenService.revokeById(verificationToken.id!!)
        userService.verifyUserEmail(email)

        publishMailEvent(
            SendMailMessageEvent(
                receiver = email,
                type = MailMessageType.GREETING,
                data = mapOf("receiverEmail" to email, "domain" to coAssembleProperties.domain)
            )
        )

        return userService.me(email)
    }

    override suspend fun forgotPasswordRequest(
        email: String
    ) {
        val user = userService.findByEmail(email) ?: throw AuthProcessingException(
            "User with email $email not found",
            HttpStatus.NOT_FOUND
        )

        if (user.provider != AuthType.MANUAL) {
            throw AuthProcessingException(
                "User with email $email not found",
                HttpStatus.NOT_FOUND
            )
        }

        verificationTokenService.generateForUser(email, VerificationTokenType.FORGOT_PASSWORD)
            .also { token ->
                publishMailEvent(
                    SendMailMessageEvent(
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

    override suspend fun forgotPasswordConfirm(forgotPasswordConfirmation: ForgotPasswordConfirmation) {
        val verificationToken = verificationTokenService.findByValueAndUserEmail(
            forgotPasswordConfirmation.token,
            forgotPasswordConfirmation.email,
            VerificationTokenType.FORGOT_PASSWORD
        )
        verificationTokenService
            .revokeById(verificationToken.id!!)

        userService.updatePassword(
            forgotPasswordConfirmation.email,
            passwordEncoder.encode(forgotPasswordConfirmation.password)
        )

        publishMailEvent(
            SendMailMessageEvent(
                receiver = forgotPasswordConfirmation.email,
                type = MailMessageType.PASSWORD_CHANGED,
                data = mapOf(
                    "receiverEmail" to forgotPasswordConfirmation.email,
                    "loginPageLink" to "${coAssembleProperties.domain}/auth/sign-in"
                )
            )
        )
    }

    override suspend fun me(auth: Authentication): UserDto =
        when (val principal = auth.principal) {
            is CoAssembleUserDetails -> userService.me(principal.username)
            else -> throw AuthProcessingException("Invalid principal type", HttpStatus.INTERNAL_SERVER_ERROR)
        }

    private fun constructMailVerificationLink(verificationToken: String, email: String): String =
        "${coAssembleProperties.mailLinkPrefix}/verify-email?email=$email&token=$verificationToken"

    private fun publishMailEvent(event: SendMailMessageEvent) = eventBus.publishEvent(event)
}

