package kz.danekerscode.coassembleapi.core.mapper

import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
import kz.danekerscode.coassembleapi.model.entity.User
import org.mapstruct.Mapper

@Mapper
interface UserMapper {

    fun toMeResponse(user: User): UserDto

}