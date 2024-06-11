package kz.danekerscode.coassembleapi.security.oauth2

import kz.danekerscode.coassembleapi.config.CoAssembleConstants.Companion.OAUTH2_PRINCIPAL_AVATAR_URL
import kz.danekerscode.coassembleapi.core.helper.GithubApiClient
import kz.danekerscode.coassembleapi.model.entity.Avatar
import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.model.enums.SecurityRole
import kz.danekerscode.coassembleapi.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.net.URI

@Component
class CoAssembleAuthenticationSuccessHandler(
    private val userService: UserService,
    private val githubApiClient: GithubApiClient
) : ServerAuthenticationSuccessHandler {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange?,
        authentication: Authentication?
    ): Mono<Void> {
        if (authentication !is OAuth2AuthenticationToken) {
            return Mono.empty()
        }

        val principal = authentication.principal
        val username = principal.name
        val registrationId = authentication.authorizedClientRegistrationId

        return githubApiClient.getUserEmail(registrationId, username)
            .filter { !it.isNullOrBlank() }
            .flatMap { userEmail ->
                log.info("Fetched user email from GitHub API: {}", userEmail)
                val provider = AuthType.forClientRegistrationId(registrationId) ?: return@flatMap Mono.empty<Void>()

                userService.existsByEmailAndProvider(userEmail, provider)
                    .flatMap { userAlreadyExists ->
                        if (userAlreadyExists) {
                            log.info("User with email: {} and provider: {} already exists", userEmail, provider)
                            Mono.empty()
                        } else {
                            log.info(
                                "User with email: {} and provider: {} does not exist. Creating new user",
                                userEmail,
                                provider
                            )

                            val user = User(
                                username = userEmail,
                                email = userEmail,
                                provider = provider,
                                roles = mutableListOf(SecurityRole.ROLE_USER),
                                emailVerified = true,
                            )

                            if (principal.attributes.containsKey(OAUTH2_PRINCIPAL_AVATAR_URL)) {
                                user.image = Avatar(
                                    uri = URI.create(principal.attributes[OAUTH2_PRINCIPAL_AVATAR_URL].toString())
                                )
                            }

                            userService.save(user)
                                .then()
                        }
                    }
            }
            .then(Mono.fromRunnable {
                log.info("User with email: {} and provider: {} authenticated successfully", username, registrationId)

                webFilterExchange?.exchange?.response?.apply {
                    statusCode = HttpStatus.FOUND
                    headers.location = URI.create("/api/v1/auth/me")
                }
            })
    }
}
