package kz.danekerscode.coassembleapi.service.impl

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
    private val mailService: MailService,
    private val authenticationManager: AuthenticationManager,
    private val securityContextRepository: SecurityContextRepository,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val coAssembleProperties: CoAssembleProperties,
    private val verificationTokenService: VerificationTokenService
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
            val args = SendMailMessageArgs(
                email,
                type.mailMessageType,
                mapOf(
                    "receiverEmail" to email,
                    "verificationTokenTtl" to coAssembleProperties.verificationTokenTtl.toMinutes()
                        .toString(),
                    "link" to constructMailVerificationLink(it.value, email)
                )
            )
            mailService.sendMailMessage(args)
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

    override suspend fun verifyEmail(token: String, email: String): UserDto {
        val verificationToken = verificationTokenService.findByValueAndUserEmail(
            token,
            email,
            VerificationTokenType.MAIL_VERIFICATION
        ).also { it.checkValidation() }

        verificationTokenService.revokeById(verificationToken.id!!)
        userService.verifyUserEmail(email)
        sendGreetingEmail(verificationToken.userEmail)
        return userService.me(email)
    }

    private suspend fun sendGreetingEmail(email: String) =
        mailService.sendMailMessage(
            SendMailMessageArgs(
                receiver = email,
                type = MailMessageType.GREETING,
                data = mapOf("receiverEmail" to email, "domain" to coAssembleProperties.domain)
            )
        )

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

        verificationTokenService.generateForUser(email, VerificationTokenType.FORGOT_PASSWORD).also { token ->
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

        sendPasswordChangedEmail(email = forgotPasswordConfirmation.email)
    }


    override suspend fun me(auth: Authentication): UserDto =
        when (val principal = auth.principal) {
            is CoAssembleUserDetails -> userService.me(principal.username)
            else -> throw AuthProcessingException("Invalid principal type", HttpStatus.INTERNAL_SERVER_ERROR)
        }


    private suspend fun sendPasswordChangedEmail(email: String) =
        mailService.sendMailMessage(
            SendMailMessageArgs(
                receiver = email,
                type = MailMessageType.PASSWORD_CHANGED,
                data = mapOf(
                    "receiverEmail" to email,
                    "loginPageLink" to "${coAssembleProperties.domain}/auth/sign-in"
                )
            )
        )

    private fun constructMailVerificationLink(verificationToken: String, email: String): String =
        "${coAssembleProperties.mailLinkPrefix}/verify-email?email=$email&token=$verificationToken"

}

