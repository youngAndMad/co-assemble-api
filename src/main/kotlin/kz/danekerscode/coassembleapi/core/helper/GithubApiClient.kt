package kz.danekerscode.coassembleapi.core.helper

import org.springframework.cache.annotation.Cacheable
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

data class EmailDetails(val email: String, val verified: Boolean, val primary: Boolean)

@Component
class GithubApiClient(
    private var authorizedClientService: OAuth2AuthorizedClientService,
    private var githubWebClient: RestClient,
) : IdentityProviderClient {

    /**
     * Fetch user email from identity provider
     * @param clientRegistrationId - client registration id
     * @param principalName - principal name
     * */
    @Cacheable(key = "{#clientRegistrationId, #principalName}", value = ["github-user-email"])
    override suspend fun getUserEmail(clientRegistrationId: String, principalName: String): String {
        val authorizedClient = authorizedClientService
            .loadAuthorizedClient<OAuth2AuthorizedClient>(clientRegistrationId, principalName)
        val accessToken = authorizedClient.accessToken.tokenValue

        return githubWebClient
            .get()
            .uri("/user/emails")
            .headers { it.setBearerAuth(accessToken) }
            .retrieve()
            .body(Array<EmailDetails>::class.java)
            ?.first { it.verified && it.primary }
            ?.email!!
    }
}