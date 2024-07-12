package kz.danekerscode.coassembleapi.features.user.domain.service

import kotlinx.coroutines.flow.Flow
import kz.danekerscode.coassembleapi.features.auth.representation.dto.RegistrationRequest
import kz.danekerscode.coassembleapi.features.user.representation.dto.UserDto
import kz.danekerscode.coassembleapi.features.user.representation.dto.UserSearchCriteria
import kz.danekerscode.coassembleapi.features.user.data.entity.User
import kz.danekerscode.coassembleapi.features.auth.data.enums.AuthType
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.user.representation.dto.UpdateUserRequest
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

    suspend fun findById(id: String): User

    suspend fun updatePassword(email: String, updatedPassword: String)

    suspend fun me(email: String): UserDto

    suspend fun createAdmin(email: String, password: String)

    suspend fun uploadAvatar(currentUser: CoAssembleUserDetails, file: MultipartFile)

    suspend fun deleteAvatar(currentUser: CoAssembleUserDetails)

    fun filterUsers(criteria: UserSearchCriteria): Flow<UserDto>

    suspend fun updateProfile(currentUser: CoAssembleUserDetails, updateUserRequest: UpdateUserRequest): UserDto
}