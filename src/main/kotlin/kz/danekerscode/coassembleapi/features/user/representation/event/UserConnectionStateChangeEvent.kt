package kz.danekerscode.coassembleapi.features.user.representation.event

data class UserConnectionStateChangeEvent(
    val userId: String,
    val online: Boolean
)
