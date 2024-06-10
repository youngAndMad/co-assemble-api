package kz.danekerscode.coassembleapi.config

class CoAssembleConstants {
    companion object {
        const val OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request"
        const val REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri"

        val INSECURE_ENDPOINTS = arrayOf(
            "/error",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/forgot-password/request/**",
            "/oauth2/**",
            "/swagger-ui.html",
        )

    }
}