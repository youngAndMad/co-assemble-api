package kz.danekerscode.coassembleapi.service

import kz.danekerscode.coassembleapi.model.payload.SendMailMessageArgs
import reactor.core.publisher.Mono

/**
 * Service for sending mail messages
 * */
interface MailService {

    /**
     * Send mail message
     * @param sendMailMessageArgs arguments for sending mail message
     * @return Mono with the result of sending
     * */
    fun sendMailMessage(
        sendMailMessageArgs: SendMailMessageArgs
    ): Mono<Void>

}