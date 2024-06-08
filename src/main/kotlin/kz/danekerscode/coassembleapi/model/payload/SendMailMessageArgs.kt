package kz.danekerscode.coassembleapi.model.payload

import kz.danekerscode.coassembleapi.model.enums.MailMessageType

data class SendMailMessageArgs(
    val receiver: String,
    val type: MailMessageType,
    val data: Map<String, Any>
)
