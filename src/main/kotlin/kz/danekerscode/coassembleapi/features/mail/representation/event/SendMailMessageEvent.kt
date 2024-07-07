package kz.danekerscode.coassembleapi.features.mail.representation.event

import kz.danekerscode.coassembleapi.features.mail.data.enums.MailMessageType

data class SendMailMessageEvent(
    val receiver: String,
    val type: MailMessageType,
    val data: Map<String, Any>
)
