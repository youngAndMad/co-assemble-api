package kz.danekerscode.coassembleapi.service

import kotlinx.coroutines.flow.Flow
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
import kz.danekerscode.coassembleapi.model.dto.user.UserSearchCriteria
import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.security.CoAssembleUserDetails
import org.springframework.web.multipart.MultipartFile

interface UserService {

    suspend fun existsByEmailAndProvider(
        username: String,
        provider: AuthType
    ): Boolean

    suspend fun save(user: User): User

    suspend fun createUser(
        registerRequest: RegistrationRequest,
        password: String
    ): User

    suspend fun verifyUserEmail(email: String): User

    suspend fun findByEmail(email: String): User?

    suspend fun updatePassword(email: String, updatedPassword: String)

    suspend fun me(email: String): UserDto

    suspend fun createAdmin(email: String, password: String)

    suspend fun uploadAvatar(currentUser: CoAssembleUserDetails, file: MultipartFile)

    suspend fun deleteAvatar(currentUser: CoAssembleUserDetails)

    fun filterUsers(criteria: UserSearchCriteria): Flow<UserDto>
}