package kz.danekerscode.coassembleapi.core.helper

import org.springframework.cache.annotation.Cacheable
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

data class EmailDetails(val email: String, val verified: Boolean, val primary: Boolean)

@Component
class GithubApiClient(
    private var authorizedClientService: ReactiveOAuth2AuthorizedClientService,
    private var githubWebClient: WebClient,
) : IdentityProviderClient {

    /**
     * Fetch user email from identity provider
     * @param clientRegistrationId - client registration id
     * @param principalName - principal name
     * */
    @Cacheable(key = "{#clientRegistrationId, #principalName}", value = ["github-user-email"])
    override fun getUserEmail(clientRegistrationId: String, principalName: String): Mono<String> =
        authorizedClientService
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
}