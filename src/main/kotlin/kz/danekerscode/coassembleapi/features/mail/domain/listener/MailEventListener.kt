package kz.danekerscode.coassembleapi.features.mail.domain.listener

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kz.danekerscode.coassembleapi.features.mail.domain.service.MailService
import kz.danekerscode.coassembleapi.features.mail.representation.event.SendMailMessageEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class MailEventListener(
    private val applicationScope: CoroutineScope,
    private val mailService: MailService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun handleMailEvent(event: SendMailMessageEvent) {
        applicationScope.launch {
            log.info("Mail event received: $event")
            mailService.sendMailMessage(event)
        }
    }
}
