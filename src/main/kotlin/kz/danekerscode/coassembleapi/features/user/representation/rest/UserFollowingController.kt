package kz.danekerscode.coassembleapi.features.user.representation.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import kz.danekerscode.coassembleapi.core.representation.dto.IdResult
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.user.domain.service.UserFollowingService
import kz.danekerscode.coassembleapi.features.user.representation.dto.UserDto
import kz.danekerscode.coassembleapi.features.user.representation.dto.UserFollowingCount
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "User Followings")
@RestController
@RequestMapping("/api/v1/users/following/{userId}")
class UserFollowingController(
    private val userFollowingService: UserFollowingService,
) {
    /**
     * Endpoint for following user
     * @param userId User ID to follow
     * @param currentUser Current authenticated user. Will be injected by Spring Security
     * @return ID of the created following
     * @author Daneker
     * 12.07.2024
     */
    @PostMapping
    @Operation(summary = "Follow user. Returns ID of the created following")
    suspend fun followUser(
        @PathVariable userId: String,
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails,
    ): IdResult = userFollowingService.followUser(currentUser, userId)

    /**
     * Endpoint for unfollowing user
     * @param userId User ID to unfollow
     * @param currentUser Current authenticated user. Will be injected by Spring Security
     * @return Empty response with 204 status code if user was unfollowed successfully
     * @author Daneker
     * 12.07.2024
     */
    @DeleteMapping
    @Operation(summary = "Unfollow user")
    suspend fun unfollowUser(
        @PathVariable userId: String,
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails,
    ) = userFollowingService.unfollowUser(currentUser, userId)

    /**
     * Endpoint for getting stats for user followings
     * @param userId User ID to get stats for
     * @return [UserFollowingCount] Stats for user followings
     * @author Daneker
     * 12.07.2024
     */
    @GetMapping("/count")
    @Operation(summary = "Get stats for user followings")
    suspend fun getFollowingCount(
        @PathVariable userId: String,
    ): UserFollowingCount = userFollowingService.getUserFollowingCount(userId)

    /**
     * Endpoint for getting user followings
     * @param userId User ID to get followings for
     * @return [UserDto] Flow of user followings
     * @see UserDto for user representation
     * @see Flow for asynchronous data stream
     */
    @GetMapping
    @Operation(summary = "Get user followings")
    fun getUserFollowings(
        @PathVariable userId: String,
    ): Flow<UserDto> = userFollowingService.getUserFollowing(userId)

    /**
     * Endpoint for getting user followers
     * @param userId User ID to get followers for
     * @return [UserDto] Flow of user followers
     * @see UserDto for user representation
     * @see Flow for asynchronous data stream
     */
    @GetMapping("/followers")
    @Operation(summary = "Get user followers")
    fun getUserFollowers(
        @PathVariable userId: String,
    ): Flow<UserDto> = userFollowingService.getUserFollowers(userId)
}
