package kz.danekerscode.coassembleapi.features.mail.domain.service

import kz.danekerscode.coassembleapi.features.mail.representation.event.SendMailMessageEvent

/**
 * Service for sending mail messages
 * */
interface MailService {

    /**
     * Send mail message
     * @param sendMailMessageEvent arguments for sending mail message
     * @return the result of sending
     * */
    suspend fun sendMailMessage(
        sendMailMessageEvent: SendMailMessageEvent
    )

}