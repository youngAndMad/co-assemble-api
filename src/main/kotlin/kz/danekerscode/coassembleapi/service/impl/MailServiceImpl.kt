package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.model.payload.SendMailMessageArgs
import kz.danekerscode.coassembleapi.service.MailService
import kz.danekerscode.coassembleapi.service.TemplateService
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets


@Service
class MailServiceImpl(
    private var mailSender: JavaMailSender,
    private var templateService: TemplateService
) : MailService {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun sendMailMessage(
        sendMailMessageArgs: SendMailMessageArgs
    ): Mono<Void> {
        return templateService
            .getTemplate(sendMailMessageArgs.type.templateName)
            .map {
                val msg = mailSender.createMimeMessage()

                val helper = MimeMessageHelper(
                    msg,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
                )

                val html = FreeMarkerTemplateUtils.processTemplateIntoString(
                    it,
                    sendMailMessageArgs.data
                )

                helper.setText(html, true)
                helper.setTo(sendMailMessageArgs.receiver)
                helper.setFrom("kkraken2005@gmail.com") // todo set sender
                helper.setSubject(sendMailMessageArgs.type.subject)

                msg
            }
            .doOnSuccess { msg ->
                mailSender.send(msg)

                log.info(
                    "Successfully delivered mail message. Receiver: {}, Type: {}",
                    sendMailMessageArgs.receiver,
                    sendMailMessageArgs.type
                )
            }
            .then()
    }
}