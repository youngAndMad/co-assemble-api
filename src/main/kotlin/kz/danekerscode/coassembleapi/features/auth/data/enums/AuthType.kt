package kz.danekerscode.coassembleapi.features.auth.data.enums

enum class AuthType {
    GITHUB,
    MANUAL,
    ;

    companion object {
        fun forClientRegistrationId(clientRegistrationId: String): AuthType? =
            entries
                .firstOrNull { it.name.equals(clientRegistrationId, ignoreCase = true) }
    }
}
