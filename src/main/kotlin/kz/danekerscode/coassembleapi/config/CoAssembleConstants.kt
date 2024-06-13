package kz.danekerscode.coassembleapi.config

class CoAssembleConstants {
    companion object {
        const val OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request"
        const val REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri"
        const val OAUTH2_PRINCIPAL_AVATAR_URL = "avatar_url"

        val INSECURE_ENDPOINTS = arrayOf(
            "/error",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/forgot-password/request/**",
            "/api/v1/auth/verify-email/**",
            "/oauth2/**",
            "/swagger-ui.html",
        )

    }
}