package kz.danekerscode.coassembleapi.security.oauth2

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CoAssembledServerLogoutSuccessHandler(
    private val clientRegistrationRepository: ReactiveClientRegistrationRepository
) : ServerLogoutSuccessHandler {
    override fun onLogoutSuccess(exchange: WebFilterExchange?, authentication: Authentication?): Mono<Void> {
        val oidcLogoutSuccessHandler = OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository)

        // Sets the location that the End-User's User Agent will be redirected to
        // after the logout has been performed at the Provider
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}")
        return oidcLogoutSuccessHandler.onLogoutSuccess(exchange, authentication)
    }
}