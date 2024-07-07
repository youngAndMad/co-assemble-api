package kz.danekerscode.coassembleapi.core.runner

import kotlinx.coroutines.runBlocking
import kz.danekerscode.coassembleapi.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.features.user.domain.service.UserService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class AdminInitializer(
    private val coAssembleProperties: CoAssembleProperties,
    private val userService: UserService
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) = runBlocking {
        userService.createAdmin(
            coAssembleProperties.adminEmail,
            coAssembleProperties.adminPassword
        )
    }
}