package kz.danekerscode.coassembleapi.model.enums

enum class MailMessageType(
    var templateName: String,
    var subject: String
) {
    MAIL_CONFIRMATION("mail_confirmation.ftl", "Mail confirmation Coassemble"),
    GREETING("greeting.ftl", "Greeting Coassemble"),
}