package kz.danekerscode.coassembleapi.features.mail.domain.service.impl

import kz.danekerscode.coassembleapi.features.mail.domain.service.MailService
import kz.danekerscode.coassembleapi.features.template.domain.service.TemplateService
import kz.danekerscode.coassembleapi.features.mail.representation.payload.SendMailMessageArgs
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import java.nio.charset.StandardCharsets

@Service
class MailServiceImpl(
    private var mailSender: JavaMailSender,
    private var templateService: TemplateService
) : MailService {

    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun sendMailMessage(
        sendMailMessageArgs: SendMailMessageArgs
    ): Unit =
        templateService
            .getTemplate(sendMailMessageArgs.type.templateName).let {
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


                //                mailSender.send(msg) todo uncomment
            }.let {
                log.info(
                    "Successfully delivered mail message. Receiver: {}, Type: {}",
                    sendMailMessageArgs.receiver,
                    sendMailMessageArgs.type
                )
            }
}