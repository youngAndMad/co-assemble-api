package kz.danekerscode.coassembleapi.features.user.representation.rest

import kz.danekerscode.coassembleapi.features.user.representation.dto.UserSearchCriteria
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.user.domain.service.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/avatar")
    suspend fun uploadAvatar(
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails,
        @RequestParam avatar: MultipartFile
    ) = userService.uploadAvatar(currentUser, avatar)

    @DeleteMapping("/avatar")
    suspend fun deleteAvatar(
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails
    ) = userService.deleteAvatar(currentUser)

    @PostMapping("/filter")
    suspend fun filterUsers(@RequestBody criteria: UserSearchCriteria) = userService.filterUsers(criteria)
}