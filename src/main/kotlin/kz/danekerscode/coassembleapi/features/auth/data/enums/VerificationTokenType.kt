package kz.danekerscode.coassembleapi.features.auth.data.enums

import kz.danekerscode.coassembleapi.features.mail.data.enums.MailMessageType

enum class VerificationTokenType(
    val mailMessageType: MailMessageType
) {
    MAIL_VERIFICATION(MailMessageType.MAIL_CONFIRMATION),
    PASSWORD_RESET(MailMessageType.PASSWORD_CHANGED),
    TWO_FACTOR_AUTHENTICATION(MailMessageType.MAIL_CONFIRMATION),
    FORGOT_PASSWORD(MailMessageType.FORGOT_PASSWORD);

}