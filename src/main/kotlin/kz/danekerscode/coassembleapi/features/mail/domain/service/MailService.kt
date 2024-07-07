package kz.danekerscode.coassembleapi.features.mail.domain.service

import kz.danekerscode.coassembleapi.features.mail.representation.payload.SendMailMessageArgs

/**
 * Service for sending mail messages
 * */
interface MailService {

    /**
     * Send mail message
     * @param sendMailMessageArgs arguments for sending mail message
     * @return the result of sending
     * */
    suspend fun sendMailMessage(
        sendMailMessageArgs: SendMailMessageArgs
    )

}