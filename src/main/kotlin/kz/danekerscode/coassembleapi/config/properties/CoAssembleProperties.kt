package kz.danekerscode.coassembleapi.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "coassemble")
class CoAssembleProperties(
    val domain: String,
    val verificationTokenTtl: Duration,
    val mailLinkPrefix: String,
    val adminEmail: String,
    val adminPassword: String
)