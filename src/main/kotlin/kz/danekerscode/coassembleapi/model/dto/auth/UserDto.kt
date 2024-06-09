package kz.danekerscode.coassembleapi.model.dto.auth

import kz.danekerscode.coassembleapi.model.entity.TechStackItem
import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.model.enums.SecurityRole

data class UserDto(
    var id: String,
    var email: String,
    var username: String,
    var provider: AuthType,
    var techStack: List<TechStackItem> = emptyList(),
    var roles: List<SecurityRole>,
    var emailVerified: Boolean = false
)
