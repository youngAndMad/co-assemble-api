package kz.danekerscode.coassembleapi.security.oauth2

import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.model.enums.SecurityRole
import kz.danekerscode.coassembleapi.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

data class EmailDetails(val email: String, val verified: Boolean, val primary: Boolean)

@Component
class CoAssembleAuthenticationSuccessHandler(
    private var authorizedClientService: ReactiveOAuth2AuthorizedClientService,
    private var githubWebClient: WebClient,
    private var userService: UserService
) : ServerAuthenticationSuccessHandler {

    private var log: Logger = LoggerFactory.getLogger(this.javaClass::class.java)

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange?,
        authentication: Authentication?
    ): Mono<Void> {
        authentication ?: return Mono.empty()

        when (authentication) {
            is OAuth2AuthenticationToken -> {
                val principal = authentication.principal
                val username = principal.name

                val registrationId = authentication.authorizedClientRegistrationId
                val userEmail = this.fetchUserEmailFromGitHubApi(registrationId, username)

                if (!userEmail.isNullOrBlank()) {
                    log.info("Fetched user email from GitHub API: {}", userEmail)
                    val provider = AuthType.forClientRegistrationId(registrationId)

                    if (provider != null) {
                        userService.existsByEmailAndProvider(userEmail, provider)
                            .subscribe { userAlreadyExists ->
                                run {
                                    if (!userAlreadyExists) {
                                        log.info(
                                            "User with email: {} and provider: {} does not exist. Creating new user",
                                            userEmail,
                                            provider
                                        )

                                        val user = User()
                                        user.email = userEmail
                                        user.provider = provider
                                        user.roles = mutableListOf(SecurityRole.ROLE_USER)
                                        user.username = username
                                        userService.save(user).subscribe()

                                    } else {
                                        log.info(
                                            "User with email: {} and provider: {} already exists",
                                            userEmail,
                                            provider
                                        )
                                    }
                                }
                            }
                    }

                }
            }
        }

        return Mono.empty()
    }

    private fun fetchUserEmailFromGitHubApi(
        clientRegistrationId: String,
        principalName: String
    ): String? {
        return authorizedClientService
            .loadAuthorizedClient<OAuth2AuthorizedClient>(clientRegistrationId, principalName)
            .flatMap { authorizedClient ->
                val accessToken = authorizedClient.accessToken.tokenValue

                githubWebClient
                    .get()
                    .uri("/user/emails")
                    .headers { it.setBearerAuth(accessToken) }
                    .retrieve()
                    .bodyToFlux(EmailDetails::class.java)
                    .filter { it.verified && it.primary }
                    .map { it.email }
                    .next()
            }
            .block() // todo delete blocking api
    }

}