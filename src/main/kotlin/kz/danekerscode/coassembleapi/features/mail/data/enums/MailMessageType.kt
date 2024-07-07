package kz.danekerscode.coassembleapi.features.mail.data.enums

enum class MailMessageType(
    var templateName: String,
    var subject: String
) {
    MAIL_CONFIRMATION("mail_confirmation.ftl", "Mail confirmation Coassemble"),
    GREETING("greeting.ftl", "Greeting Coassemble"),
    FORGOT_PASSWORD("forgot_password.ftl", "Forgot password Coassemble"),
    PASSWORD_CHANGED("password_changed.ftl", "Password changed Coassemble")
}