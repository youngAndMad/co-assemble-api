package kz.danekerscode.coassembleapi.security

import kz.danekerscode.coassembleapi.config.CoAssembleConstants.Companion.INSECURE_ENDPOINTS
import kz.danekerscode.coassembleapi.core.helper.GithubApiClient
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Component
class CoAssembleAuthFilter(
    private var githubApiClient: GithubApiClient,
    private var coAssembleUserDetailService: ReactiveUserDetailsService
) : WebFilter {

    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain
    ): Mono<Void> =
        exchangeForFiltering(exchange, chain)
            .flatMap {
                ReactiveSecurityContextHolder.getContext()
                    .flatMap { securityContext ->
                        val authentication = securityContext.authentication
                        if (authentication is OAuth2AuthenticationToken) {
                            val principal = authentication.principal
                            val username = principal.name
                            val registrationId = authentication.authorizedClientRegistrationId

                            githubApiClient.getUserEmail(registrationId, username)
                                .flatMap { email ->
                                    coAssembleUserDetailService.findByUsername(email)
                                        .flatMap { userDetails ->
                                            val newAuth = UsernamePasswordAuthenticationToken(
                                                userDetails,
                                                null,
                                                userDetails.authorities
                                            )
                                            ReactiveSecurityContextHolder
                                                .getContext().flatMap { context ->
                                                    context.authentication = newAuth
                                                    Mono.just(context)
                                                }
                                        }
                                }
                        } else {
                            chain.filter(exchange)
                        }
                    }
                    .then()


            }


    private fun exchangeForFiltering(exchange: ServerWebExchange, chain: WebFilterChain): Mono<ServerWebExchange> =
        Mono.just(exchange)
            .filterWhen { shouldFilter(it) }
            .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))

    private fun shouldFilter(exchange: ServerWebExchange): Mono<Boolean> =
        NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers(*INSECURE_ENDPOINTS))
            .matches(exchange)
            .map(ServerWebExchangeMatcher.MatchResult::isMatch)
}