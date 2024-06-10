package kz.danekerscode.coassembleapi.core.mapper

import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.model.enums.SecurityRole
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import java.util.Arrays

@Mapper(imports = [AuthType::class, SecurityRole::class, Arrays::class, ArrayList::class])
interface UserMapper {
    fun toMeResponse(user: User): UserDto

    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "username", source = "email")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "techStack", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "roles", expression = "java(new ArrayList<>(Arrays.stream(SecurityRole.values()).toList()))")
    @Mapping(target = "provider", expression = "java(AuthType.MANUAL)")
    fun toAdmin(email: String, password: String): User

}