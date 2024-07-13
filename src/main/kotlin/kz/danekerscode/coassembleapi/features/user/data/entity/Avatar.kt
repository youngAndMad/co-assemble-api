package kz.danekerscode.coassembleapi.features.user.data.entity

import kz.danekerscode.coassembleapi.core.data.entity.BaseEntity
import java.io.Serializable
import java.net.URL

class Avatar(
    val id: String? = null, // will be provided for internal avatars
    val url: URL,
    val external: Boolean = false,
) : BaseEntity(), Serializable
