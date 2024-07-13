package kz.danekerscode.coassembleapi.core.security.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kz.danekerscode.coassembleapi.core.config.CoAssembleConstants.Companion.OAUTH2_PRINCIPAL_AVATAR_URL
import kz.danekerscode.coassembleapi.core.domain.helper.IdentityProviderClient
import kz.danekerscode.coassembleapi.features.auth.data.enums.AuthType
import kz.danekerscode.coassembleapi.features.user.data.entity.Avatar
import kz.danekerscode.coassembleapi.features.user.data.entity.User
import kz.danekerscode.coassembleapi.features.user.data.enums.SecurityRole
import kz.danekerscode.coassembleapi.features.user.domain.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.net.URL
import java.util.*

@Component
class CoAssembleAuthenticationSuccessHandler(
    private val userService: UserService,
    private val githubApiClient: IdentityProviderClient,
    private val applicationScope: CoroutineScope,
) : AuthenticationSuccessHandler {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?,
    ) {
        authentication ?: return

        if (authentication !is OAuth2AuthenticationToken) {
            return
        }

        val principal = authentication.principal
        val username = principal.name
        val registrationId = authentication.authorizedClientRegistrationId

        applicationScope.launch launch@{
            val userEmail = githubApiClient.getUserEmail(registrationId, username)
            if (userEmail.isNotBlank()) {
                log.info("Fetched user email from GitHub API: {}", userEmail)
                val provider = AuthType.forClientRegistrationId(registrationId) ?: return@launch

                val userAlreadyExists = userService.existsByEmailAndProvider(userEmail, provider)

                if (userAlreadyExists) {
                    log.info("User with email: {} and provider: {} already exists", userEmail, provider)
                } else {
                    log.info(
                        "User with email: {} and provider: {} does not exist. Creating new user",
                        userEmail,
                        provider,
                    )

                    val user =
                        User(
                            username = userEmail,
                            email = userEmail,
                            provider = provider,
                            roles = mutableListOf(SecurityRole.ROLE_USER),
                            emailVerified = true,
                        )

                    if (principal.attributes.containsKey(OAUTH2_PRINCIPAL_AVATAR_URL)) {
                        user.image =
                            Avatar(
                                external = true,
                                id = UUID.randomUUID().toString(),
                                url = URL(principal.attributes[OAUTH2_PRINCIPAL_AVATAR_URL].toString()),
                            )
                    }

                    userService.save(user).also {
                        log.info("User created after successfully oauth2 login: {}", it.id)
                    }
                }
            }

            response?.apply {
                status = HttpServletResponse.SC_FOUND
                setHeader("Location", "/api/v1/auth/me")
            }
        }
    }
}
