package kz.danekerscode.coassembleapi.security.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component

@Component
class CoAssembleLogoutSuccessHandler(
    clientRegistrationRepository: ClientRegistrationRepository
) : LogoutSuccessHandler {

    private val _oidcClientInitiatedLogoutSuccessHandler =
        OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository)
    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        _oidcClientInitiatedLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}")
    }

    override fun onLogoutSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        _oidcClientInitiatedLogoutSuccessHandler.onLogoutSuccess(request, response, authentication)
        log.info("User {} has been logged out" , authentication?.name)
    }
}