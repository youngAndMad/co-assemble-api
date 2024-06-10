package kz.danekerscode.coassembleapi.core.mapper

import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.model.enums.SecurityRole
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(imports = [AuthType::class])
interface UserMapper {

    fun toMeResponse(user: User): UserDto

    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "username", source = "email")
    @Mapping(target = "roles", expression = "java(adminRoles())")
    @Mapping(target = "provider", expression = "java(AuthType.MANUAL)")
    fun toAdmin(email: String, password: String): User

    fun adminRoles() = listOf(SecurityRole.entries.toTypedArray())
}