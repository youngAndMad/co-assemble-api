package kz.danekerscode.coassembleapi.security.oauth2

import kz.danekerscode.coassembleapi.config.CoAssembleConstants
import kz.danekerscode.coassembleapi.utils.CookieUtils
import org.springframework.security.oauth2.client.web.server.ServerAuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class CoAssembleServerAuthorizationRequestRepository :
    ServerAuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private var cookieExpireSeconds = 180L

    override fun loadAuthorizationRequest(exchange: ServerWebExchange): Mono<OAuth2AuthorizationRequest> {
        return CookieUtils.getCookie(exchange.response, CoAssembleConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            .map { cookie -> CookieUtils.deserialize(cookie!!, OAuth2AuthorizationRequest::class.java) }
            .switchIfEmpty(Mono.empty())
    }

    override fun saveAuthorizationRequest(
        authorizationRequest: OAuth2AuthorizationRequest?,
        exchange: ServerWebExchange?
    ): Mono<Void> {
        exchange ?: return Mono.empty()

        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(exchange, CoAssembleConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            CookieUtils.deleteCookie(exchange, CoAssembleConstants.REDIRECT_URI_PARAM_COOKIE_NAME)
            return Mono.empty()
        }

        CookieUtils.addCookie(
            exchange.response, CoAssembleConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
            CookieUtils.serialize(authorizationRequest), cookieExpireSeconds
        )

        val redirectUriAfterLogin =
            exchange.request.queryParams[CoAssembleConstants.REDIRECT_URI_PARAM_COOKIE_NAME]?.firstOrNull()

        if (!redirectUriAfterLogin.isNullOrBlank()) {
            CookieUtils.addCookie(
                exchange.response,
                CoAssembleConstants.REDIRECT_URI_PARAM_COOKIE_NAME,
                redirectUriAfterLogin,
                cookieExpireSeconds
            )
        }

        return Mono.empty()
    }

    override fun removeAuthorizationRequest(request: ServerWebExchange): Mono<OAuth2AuthorizationRequest> {
        return loadAuthorizationRequest(request)
    }

    fun removeAuthorizationRequestCookies(exchange: ServerWebExchange) {
        CookieUtils.deleteCookie(exchange, CoAssembleConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        CookieUtils.deleteCookie(exchange, CoAssembleConstants.REDIRECT_URI_PARAM_COOKIE_NAME)
    }
}