package kz.danekerscode.coassembleapi.config

import kz.danekerscode.coassembleapi.config.CoAssembleConstants.Companion.INSECURE_ENDPOINTS
import kz.danekerscode.coassembleapi.security.CoAssembleAuthFilter
import kz.danekerscode.coassembleapi.security.oauth2.CoAssembleAuthenticationSuccessHandler
import kz.danekerscode.coassembleapi.security.oauth2.CoAssembleAuthorizationRequestRepository
import kz.danekerscode.coassembleapi.security.oauth2.CoAssembleLogoutSuccessHandler
import kz.danekerscode.coassembleapi.utils.isRunningInLocal
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository

@Configuration
@EnableWebSecurity
class SecurityConfig (
    private val env: Environment
){

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        coAssembleAuthenticationProvider: AuthenticationManager,
        coAssembledLogoutSuccessHandler: CoAssembleLogoutSuccessHandler,
        coAssembleAuthFilter: CoAssembleAuthFilter,
        coAssembleAuthenticationSuccessHandler: CoAssembleAuthenticationSuccessHandler,
        coAssembleAuthorizationRequestRepository: CoAssembleAuthorizationRequestRepository
    ): SecurityFilterChain {

        if (env.isRunningInLocal()) {
            http.authorizeHttpRequests {
                it.requestMatchers("/**").permitAll()
            }
        }

        http
            .csrf { it.disable() }
            .cors { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .headers { headers ->
                headers.frameOptions { frameOptions ->
                    frameOptions.disable()
                }
            }
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .authenticationManager(coAssembleAuthenticationProvider)
            .authorizeHttpRequests {
                it
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .requestMatchers(*INSECURE_ENDPOINTS).permitAll()
                    .anyRequest().authenticated()
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
                    .logoutSuccessHandler(coAssembledLogoutSuccessHandler)
                    .logoutUrl("/api/v1/auth/logout")
            }
            .addFilterBefore(coAssembleAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .oauth2Login { oauth2 ->
                oauth2.successHandler(coAssembleAuthenticationSuccessHandler)
                    .permitAll()
                    .authorizationEndpoint { authEndpoint ->
                        authEndpoint.authorizationRequestRepository(coAssembleAuthorizationRequestRepository)
                    }
            }
        return http.build()
    }

    @Bean
    fun securityContextRepository(): SecurityContextRepository =
        HttpSessionSecurityContextRepository()

    @Bean
    fun authenticationProvider(
        passwordEncoder: PasswordEncoder,
        userDetailsService: UserDetailsService
    ): AuthenticationProvider = DaoAuthenticationProvider().apply {
        setPasswordEncoder(passwordEncoder)
        isHideUserNotFoundExceptions = false
        setUserDetailsService(userDetailsService)
    }

    @Bean
    fun authenticationManager(
        http: HttpSecurity,
        authenticationProvider: AuthenticationProvider
    ): AuthenticationManager =
        http.getSharedObject(AuthenticationManagerBuilder::class.java)
            .authenticationProvider(authenticationProvider)
            .build()
}
