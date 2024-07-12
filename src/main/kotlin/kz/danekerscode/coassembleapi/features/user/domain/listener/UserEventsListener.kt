package kz.danekerscode.coassembleapi.features.user.domain.listener

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kz.danekerscode.coassembleapi.features.auth.representation.event.UserLoginEvent
import kz.danekerscode.coassembleapi.features.user.domain.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class UserEventsListener(
    private val userService: UserService,
    private val applicationScope: CoroutineScope,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onUserLogin(userLoginEvent: UserLoginEvent) =
        applicationScope.launch {
            log.info("User login event received: $userLoginEvent")
            userService.findByEmail(userLoginEvent.userEmail)?.also {
                it.lastLoginAddress = userLoginEvent.ip
                userService.save(it)
                log.info("User last login address updated: ${it.email} -> ${it.lastLoginAddress}")
            }
        }
}
