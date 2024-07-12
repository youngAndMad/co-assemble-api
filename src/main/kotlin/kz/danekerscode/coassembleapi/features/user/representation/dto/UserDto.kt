package kz.danekerscode.coassembleapi.features.user.representation.dto

import kz.danekerscode.coassembleapi.features.auth.data.enums.AuthType
import kz.danekerscode.coassembleapi.features.techstackitem.data.entity.TechStackItem
import kz.danekerscode.coassembleapi.features.user.data.entity.Avatar
import kz.danekerscode.coassembleapi.features.user.data.enums.SecurityRole

data class UserDto(
    var id: String,
    var email: String,
    var username: String,
    var provider: AuthType,
    var techStack: List<TechStackItem> = mutableListOf(),
    var roles: List<SecurityRole>,
    var emailVerified: Boolean = false,
    var avatar: Avatar? = null,
)
