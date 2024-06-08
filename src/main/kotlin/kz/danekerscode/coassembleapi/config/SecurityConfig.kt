package kz.danekerscode.coassembleapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler
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
        coAssembleAuthenticationProvider: ReactiveAuthenticationManager
    ): SecurityWebFilterChain =
        http
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .authenticationManager(coAssembleAuthenticationProvider)
            .oauth2Client { }
            .authorizeExchange {
                it
                    .pathMatchers("/api/v1/auth/**").permitAll()
                    .pathMatchers("/api/v1/**").authenticated()
                    .anyExchange().permitAll()
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
                    .logoutSuccessHandler(HttpStatusReturningServerLogoutSuccessHandler())
                    .logoutUrl("/api/v1/auth/logout")
            }

            .build()

    @Bean
    fun serverSecurityContextRepository() : ServerSecurityContextRepository =
        WebSessionServerSecurityContextRepository()

}