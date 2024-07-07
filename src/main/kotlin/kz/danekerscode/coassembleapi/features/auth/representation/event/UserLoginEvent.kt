package kz.danekerscode.coassembleapi.features.auth.representation.event

data class UserLoginEvent(
    val userEmail: String,
    val ip: String,
)
