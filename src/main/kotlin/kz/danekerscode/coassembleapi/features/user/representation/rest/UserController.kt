package kz.danekerscode.coassembleapi.features.user.representation.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.user.data.mapper.UserMapper
import kz.danekerscode.coassembleapi.features.user.domain.service.UserService
import kz.danekerscode.coassembleapi.features.user.representation.dto.UserDto
import kz.danekerscode.coassembleapi.features.user.representation.dto.UserSearchCriteria
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Users")
@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
    private val userMapper: UserMapper,
) {
    /**
     * Endpoint for uploading user avatar
     * @param currentUser Current authenticated user. Will be injected by Spring Security
     * @param avatar Avatar file to upload
     * @author Daneker
     * 12.07.2024
     */
    @Operation(summary = "Upload user avatar")
    @PostMapping("/avatar")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun uploadAvatar(
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails,
        @RequestParam avatar: MultipartFile,
    ) = userService.uploadAvatar(currentUser, avatar)

    /**
     * Endpoint for deleting user avatar
     * @param currentUser Current authenticated user. Will be injected by Spring Security
     * @return Empty response with 204 status code if avatar was deleted successfully
     * @author Daneker
     * 12.07.2024
     */
    @Operation(summary = "Delete user avatar")
    @DeleteMapping("/avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteAvatar(
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails,
    ) = userService.deleteAvatar(currentUser)

    /**
     * Endpoint for filtering users
     * @param criteria Search criteria
     * @return List of users that match the search criteria
     * @author Daneker
     * 12.07.2024
     */
    @Operation(summary = "Filter users")
    @PostMapping("/filter")
    suspend fun filterUsers(
        @RequestBody criteria: UserSearchCriteria,
    ) = userService.filterUsers(criteria)

    /**
     * Endpoint for getting current authenticated user
     * @param currentUser Current authenticated user. Will be injected by Spring Security
     * @return Current authenticated user data in UserDto format
     * @see [UserDto]
     * @author Daneker
     * 12.07.2024
     */
    @Operation(summary = "Get current user")
    @GetMapping("/me")
    suspend fun getMe(
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails,
    ): UserDto = userMapper.toUserDto(currentUser.user)
}
