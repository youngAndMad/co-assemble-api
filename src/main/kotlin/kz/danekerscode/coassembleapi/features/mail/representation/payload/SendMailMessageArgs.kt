package kz.danekerscode.coassembleapi.features.mail.representation.payload

import kz.danekerscode.coassembleapi.features.mail.data.enums.MailMessageType

data class SendMailMessageArgs(
    val receiver: String,
    val type: MailMessageType,
    val data: Map<String, Any>
)
