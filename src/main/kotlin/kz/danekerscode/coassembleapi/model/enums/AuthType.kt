package kz.danekerscode.coassembleapi.model.enums

enum class AuthType {
    GITHUB,
    MANUAL;

    companion object {
        fun forClientRegistrationId(clientRegistrationId: String): AuthType? {
            return entries
                .firstOrNull { it.name.equals(clientRegistrationId, ignoreCase = true) }
        }
    }
}