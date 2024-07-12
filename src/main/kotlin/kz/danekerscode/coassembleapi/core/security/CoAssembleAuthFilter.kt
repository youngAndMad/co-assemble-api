package kz.danekerscode.coassembleapi.core.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kz.danekerscode.coassembleapi.core.config.CoAssembleConstants.Companion.INSECURE_ENDPOINTS
import kz.danekerscode.coassembleapi.core.domain.helper.GithubApiClient
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.web.filter.OncePerRequestFilter

// @Component
class CoAssembleAuthFilter(
    private val githubApiClient: GithubApiClient,
    private val coAssembleUserDetailService: UserDetailsService,
    private val applicationScope: CoroutineScope,
) : OncePerRequestFilter() {
    private val matchers = INSECURE_ENDPOINTS.map { AntPathRequestMatcher(it) }
    private val negatedMatcher = NegatedRequestMatcher(OrRequestMatcher(matchers))

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) { // todo delete blocking
        applicationScope.launch {
            with(SecurityContextHolder.getContext()) {
                if (shouldFilter(request) && authentication is OAuth2AuthenticationToken) {
                    val oAuth2AuthenticationToken = authentication as OAuth2AuthenticationToken
                    val principal = oAuth2AuthenticationToken.principal
                    val username = principal.name
                    val registrationId = oAuth2AuthenticationToken.authorizedClientRegistrationId

                    val userEmail = githubApiClient.getUserEmail(registrationId, username)
                    val userDetails = coAssembleUserDetailService.loadUserByUsername(userEmail)
                    SecurityContextHolder.getContext().authentication =
                        UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.authorities,
                        )
                }
            }

            filterChain.doFilter(request, response)
        }
    }

    private fun shouldFilter(request: HttpServletRequest): Boolean = negatedMatcher.matches(request)
}
