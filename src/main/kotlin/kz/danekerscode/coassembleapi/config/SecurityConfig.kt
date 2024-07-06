package kz.danekerscode.coassembleapi.config

import kz.danekerscode.coassembleapi.config.CoAssembleConstants.Companion.INSECURE_ENDPOINTS
import kz.danekerscode.coassembleapi.security.CoAssembleAuthFilter
import kz.danekerscode.coassembleapi.security.oauth2.CoAssembleAuthenticationSuccessHandler
import kz.danekerscode.coassembleapi.security.oauth2.CoAssembleServerAuthorizationRequestRepository
import kz.danekerscode.coassembleapi.security.oauth2.CoAssembledServerLogoutSuccessHandler
import org.springframework.boot.autoconfigure.security.reactive.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()


    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        serverSecurityContextRepository: ServerSecurityContextRepository,
        coAssembleAuthenticationProvider: ReactiveAuthenticationManager,
        coAssembledServerLogoutSuccessHandler: CoAssembledServerLogoutSuccessHandler,
        coAssembleAuthenticationSuccessHandler: CoAssembleAuthenticationSuccessHandler,
        coAssembleAuthFilter: CoAssembleAuthFilter,
        coAssembleServerAuthorizationRequestRepository: CoAssembleServerAuthorizationRequestRepository
    ): SecurityWebFilterChain =
        http
            .headers { headers ->
                headers.frameOptions { frameOptions ->
                    frameOptions.disable()
                }
            }
            .csrf { it.disable() }
            .cors { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .authenticationManager(coAssembleAuthenticationProvider)
            .oauth2Login {
                it
                    .authenticationSuccessHandler(coAssembleAuthenticationSuccessHandler)
//                    .authorizationRequestRepository(coAssembleServerAuthorizationRequestRepository)
            }
            .authorizeExchange {
                it
                    .matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .pathMatchers(*INSECURE_ENDPOINTS).permitAll()
                    .anyExchange().authenticated()
            }
            /*
           * If you request POST /logout, then it will perform the following default operations using a series of LogoutHandlers:
           * Invalidate the HTTP session (SecurityContextLogoutHandler)
           * Clear the SecurityContextHolderStrategy (SecurityContextLogoutHandler)
           * Clear the SecurityContextRepository (SecurityContextLogoutHandler)
           * Clean up any RememberMe authentication (TokenRememberMeServices / PersistentTokenRememberMeServices)
           * Clear out any saved CSRF token (CsrfLogoutHandler)
           * Fire a LogoutSuccessEvent (LogoutSuccessEventPublishingLogoutHandler)
           * Once completed, then it will exercise its default LogoutSuccessHandler which redirects to /login?logout.
           */
            .logout {
                it
                    .logoutSuccessHandler(coAssembledServerLogoutSuccessHandler)
                    .logoutUrl("/api/v1/auth/logout")
            }
            .securityContextRepository(serverSecurityContextRepository)
            .addFilterAt(coAssembleAuthFilter, SecurityWebFiltersOrder.LAST)
            .build()

    @Bean
    fun securityContextRepository(): SecurityContextRepository =
        HttpSessionSecurityContextRepository()

}
