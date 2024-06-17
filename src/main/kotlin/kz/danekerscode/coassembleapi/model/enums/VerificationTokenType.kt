package kz.danekerscode.coassembleapi.model.enums

enum class VerificationTokenType(
    val mailMessageType: MailMessageType
) {
    MAIL_VERIFICATION(MailMessageType.MAIL_CONFIRMATION),
    PASSWORD_RESET(MailMessageType.PASSWORD_CHANGED),
    TWO_FACTOR_AUTHENTICATION(MailMessageType.MAIL_CONFIRMATION), //todo create enum
    FORGOT_PASSWORD(MailMessageType.FORGOT_PASSWORD);

}