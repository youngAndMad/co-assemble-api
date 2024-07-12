package kz.danekerscode.coassembleapi.core.database.mongo

import org.slf4j.LoggerFactory
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserAuditing : AuditorAware<String> {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun getCurrentAuditor(): Optional<String> =
        Optional
            .of(SecurityContextHolder.getContext()?.authentication?.name ?: "anonymous")
            .apply {
                log.debug("Current auditor: {}", this.get())
            }
}
