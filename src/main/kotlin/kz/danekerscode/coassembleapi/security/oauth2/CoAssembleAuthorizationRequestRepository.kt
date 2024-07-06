package kz.danekerscode.coassembleapi.security.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kz.danekerscode.coassembleapi.config.CoAssembleConstants
import kz.danekerscode.coassembleapi.utils.CookieUtils
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component

@Component
class CoAssembleAuthorizationRequestRepository :
    AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private var cookieExpireSeconds = 180

    override fun loadAuthorizationRequest(request: HttpServletRequest?): OAuth2AuthorizationRequest {
        val cookie = CookieUtils.getCookie(request, CoAssembleConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            ?: return null!!

        return CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest::class.java)
    }

    override fun removeAuthorizationRequest(
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ): OAuth2AuthorizationRequest = this.loadAuthorizationRequest(request)

    override fun saveAuthorizationRequest(
        authorizationRequest: OAuth2AuthorizationRequest?,
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ) {
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(
                request!!,
                response!!,
                CoAssembleConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME
            )
            CookieUtils.deleteCookie(request, response, CoAssembleConstants.REDIRECT_URI_PARAM_COOKIE_NAME)
            return
        }

        CookieUtils.addCookie(
            response!!, CoAssembleConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
            CookieUtils.serialize(authorizationRequest), cookieExpireSeconds
        ).let {
            val redirectUriAfterLogin = request?.getParameter(CoAssembleConstants.REDIRECT_URI_PARAM_COOKIE_NAME)
            if (!redirectUriAfterLogin.isNullOrBlank()) {
                CookieUtils.addCookie(
                    response,
                    CoAssembleConstants.REDIRECT_URI_PARAM_COOKIE_NAME,
                    redirectUriAfterLogin,
                    cookieExpireSeconds
                )
            }
        }
    }
}