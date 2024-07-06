package kz.danekerscode.coassembleapi.service.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kz.danekerscode.coassembleapi.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.core.mapper.UserMapper
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
import kz.danekerscode.coassembleapi.model.dto.user.UserSearchCriteria
import kz.danekerscode.coassembleapi.model.entity.Avatar
import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.model.enums.SecurityRole
import kz.danekerscode.coassembleapi.repository.UserRepository
import kz.danekerscode.coassembleapi.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.service.FileService
import kz.danekerscode.coassembleapi.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.nio.file.attribute.UserPrincipalNotFoundException

@Service
class UserServiceImpl(
    private var mongoTemplate: ReactiveMongoTemplate,
    private var userRepository: UserRepository,
    private var userMapper: UserMapper,
    private var passwordEncoder: PasswordEncoder,
    private var fileService: FileService,
    private var coAssembleProperties: CoAssembleProperties
) : UserService {

    private var log: Logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun existsByEmailAndProvider(
        username: String, provider: AuthType
    ): Boolean {
        log.debug("Checking if user exists by username: {} and provider: {}", username, provider)
        return userRepository.existsByEmailAndProvider(username, provider)
    }

    override suspend fun save(user: User): User = userRepository.save(user)

    override suspend fun createUser(
        registerRequest: RegistrationRequest,
        password: String
    ): User =
        this.save(
            User(
                username = registerRequest.username,
                email = registerRequest.email,
                password = passwordEncoder.encode(password),
                provider = AuthType.MANUAL,
                roles = mutableListOf(SecurityRole.ROLE_USER)
            )
        )

    override suspend fun verifyUserEmail(email: String): User {
        val user = userRepository.findByEmail(email)
            ?: throw UserPrincipalNotFoundException("User not found for email: $email")

        if (!user.emailVerified) {
            user.emailVerified = true
        }

        return this.save(user)
    }

    override suspend fun findByEmail(email: String): User? =
        userRepository.findByEmail(email)

    override suspend fun updatePassword(email: String, updatedPassword: String) {
        (userRepository.findByEmail(email)
            ?: throw UserPrincipalNotFoundException("User not found for email: $email"))
            .let { user ->
                user.password = updatedPassword
                userRepository.save(user)
            }
    }

    override suspend fun me(email: String): UserDto = (userRepository
        .findByEmail(email) ?: throw UserPrincipalNotFoundException("User not found for email: $email"))
        .let { userMapper.toUserDto(it) }

    override suspend fun createAdmin(email: String, password: String) {
        val existsByEmailAndProvider = existsByEmailAndProvider(email, AuthType.MANUAL)
        if (existsByEmailAndProvider) {
            log.info("Admin with email {} already exists", email)
            return
        }
        val user = userMapper.toAdmin(email, passwordEncoder.encode(password))
        userRepository.save(user)
    }

    override suspend fun uploadAvatar(
        currentUser: CoAssembleUserDetails,
        file: MultipartFile
    ) {
        val uploadedFileId = fileService.uploadFile(file)
        currentUser.user.let {
            it.image = constructAvatarForFileId(uploadedFileId)
            this.save(it).also { _ ->
                log.info("Avatar uploaded successfully for user: {}", it.email)
            }
        }
    }

    private fun constructAvatarForFileId(id: String?) = Avatar(
        id = id,
        uri = URI.create(constructDownloadFileUrl(id)),
        external = false
    )

    private fun constructDownloadFileUrl(id: String?) = "${coAssembleProperties.domain}/api/v1/files/download/$id"

    override suspend fun deleteAvatar(currentUser: CoAssembleUserDetails): Unit =
        currentUser.user.let {
            if (false == it.image?.external) {
                fileService.deleteFile(it.image?.id!!)
                resetUserProfileImage(it).also { _ ->
                    log.info("Avatar deleted successfully for user: {}", it.email)
                }
            } else {
                log.warn("Avatar is external for user: {} or does not exist", it.email)
            }
        }

    private suspend fun resetUserProfileImage(user: User) = user.let {
        it.image = null
        this.save(it)
    }

    override fun filterUsers(criteria: UserSearchCriteria): Flow<UserDto> {
        val query = Query()

        criteria.keyword?.let {
            query.addCriteria(
                Criteria().orOperator(
                    Criteria.where("email").regex(it, "i"),
                    Criteria.where("username").regex(it, "i")
                )
            )
        }

        criteria.stackItemType?.let {
            query.addCriteria(Criteria.where("techStack").`is`(it))
        }

        return mongoTemplate.find(query, User::class.java)
            .map { userMapper.toUserDto(it) }
            .asFlow()
    }
}