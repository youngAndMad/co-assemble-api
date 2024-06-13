package kz.danekerscode.coassembleapi.model.entity

import java.io.Serializable
import java.net.URI

class Avatar(
    val id: String? = null, // will be provided for internal avatars
    val uri: URI,
    val external: Boolean = false,
) : Serializable