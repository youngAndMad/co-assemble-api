package kz.danekerscode.coassembleapi.features.user.domain.runner

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kz.danekerscode.coassembleapi.core.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.features.user.domain.service.UserService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class AdminInitializer(
    private val coAssembleProperties: CoAssembleProperties,
    private val userService: UserService,
    private val applicationScope: CoroutineScope
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        applicationScope.launch {
            userService.createAdmin(
                coAssembleProperties.adminEmail,
                coAssembleProperties.adminPassword
            )
        }
    }
}