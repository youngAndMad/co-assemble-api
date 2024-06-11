package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.core.mapper.UserMapper
import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
import kz.danekerscode.coassembleapi.model.dto.auth.UserDto
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
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.net.URI
import java.nio.file.attribute.UserPrincipalNotFoundException

@Service
class UserServiceImpl(
    private var userRepository: UserRepository,
    private var userMapper: UserMapper,
    private var passwordEncoder: PasswordEncoder,
    private var fileService: FileService,
    private var coAssembleProperties: CoAssembleProperties
) : UserService {

    private var log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun existsByEmailAndProvider(
        username: String, provider: AuthType
    ): Mono<Boolean> {
        log.debug("Checking if user exists by username: {} and provider: {}", username, provider)
        return userRepository.existsByEmailAndProvider(username, provider)
    }

    override fun save(user: User): Mono<User> = userRepository.save(user)

    override fun createUser(
        registerRequest: RegistrationRequest,
        password: String
    ): Mono<User> =
        this.save(
            User(
                username = registerRequest.username,
                email = registerRequest.email,
                password = passwordEncoder.encode(password),
                provider = AuthType.MANUAL,
                roles = mutableListOf(SecurityRole.ROLE_USER)
            )
        )

    override fun verifyUserEmail(email: String): Mono<Void> =
        userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(UserPrincipalNotFoundException("User not found for email: $email")))
            .flatMap { user ->
                if (user.emailVerified) {
                    // Email already verified, no need to update
                    Mono.empty()
                } else {
                    // Update email verification status
                    user.emailVerified = true
                    userRepository.save(user).then()
                }
            }

    override fun findByEmail(email: String): Mono<User> = userRepository.findByEmail(email)

    override fun updatePassword(email: String, updatedPassword: String): Mono<Void> =
        userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(UserPrincipalNotFoundException("User not found for email: $email")))
            .flatMap { user ->
                user.password = updatedPassword
                userRepository.save(user).then()
            }

    override fun me(email: String): Mono<UserDto> = userRepository
        .findByEmail(email)
        .map { userMapper.toMeResponse(it) }

    override fun createAdmin(email: String, password: String): Mono<Void> =
        existsByEmailAndProvider(email, AuthType.MANUAL)
            .flatMap { exists ->
                if (exists) {
                    log.info("Admin with email {} already exists", email)
                    Mono.empty()
                } else {
                    val user = userMapper.toAdmin(email, passwordEncoder.encode(password))
                    userRepository.save(user).flatMap {
                        log.info("Admin with email {} created successfully", email)
                        Mono.empty()
                    }
                }

            }

    override fun uploadAvatar(
        currentUser: CoAssembleUserDetails,
        file: Mono<FilePart>
    ): Mono<Void> =
        fileService.uploadFile(file)
            .flatMap { id ->
                val user = currentUser.user
                user.image = constructAvatarForFileId(id)
                userRepository.save(user)
                    .then<Void?>(Mono.fromRunnable {
                        log.info("Avatar uploaded successfully for user: {}", user.email)
                    })
                    .onErrorResume {
                        log.error("Error uploading avatar for user: {}", user.email, it)
                        Mono.error(it)
                    }
            }

    private fun constructAvatarForFileId(id: String?) = Avatar(
        id = id,
        uri = URI.create(constructDownloadFileUrl(id)),
        external = false
    )

    private fun constructDownloadFileUrl(id: String?) = "${coAssembleProperties.domain}/api/v1/files/download/$id"

    override fun deleteAvatar(currentUser: CoAssembleUserDetails): Mono<Void> {
        val user = currentUser.user
        return if (!user.image?.external!!)
            fileService.deleteFile(user.image?.id!!)
                .then(Mono.defer {
                    user.image = null
                    userRepository.save(user)
                        .then<Void?>(Mono.fromRunnable {
                            log.info("Avatar deleted successfully for user: {}", user.email)
                        })
                        .onErrorResume {
                            log.error("Error deleting avatar for user: {}", user.email, it)
                            Mono.error(it)
                        }
                })
        else
            Mono.empty()
    }
}