package kz.danekerscode.coassembleapi.features.user.domain.service

import kotlinx.coroutines.flow.Flow
import kz.danekerscode.coassembleapi.core.representation.dto.IdResult
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.user.representation.dto.UserDto
import kz.danekerscode.coassembleapi.features.user.representation.dto.UserFollowingCount

interface UserFollowingService {
    /**
     * Follows a user
     * @param currentUser the current user
     * @param userId the user to follow
     * @return the id of the follow relationship
     * @author Daneker
     * 12.07.2024
     */
    suspend fun followUser(
        currentUser: CoAssembleUserDetails,
        userId: String,
    ): IdResult

    /**
     * Unfollows a user
     * @param currentUser the current user
     * @param userId the user to unfollow
     * @author Daneker
     * 12.07.2024
     */
    suspend fun unfollowUser(
        currentUser: CoAssembleUserDetails,
        userId: String,
    )

    /**
     * Gets the count of users that the user is following
     * @param userId id of user to fetch the count
     * @return the count of users that the user is following
     * @author Daneker
     * 12.07.2024
     */
    suspend fun getUserFollowingCount(userId: String): UserFollowingCount

    /**
     * Gets the followers of a user
     * @param userId id of user to fetch the followers
     * @return [Flow] of [UserDto] of the followers of the user
     * @author Daneker
     * 12.07.2024
     */
    fun getUserFollowers(userId: String): Flow<UserDto>

    /**
     * Gets the users that the user is following
     * @param userId id of user to fetch the following users
     * @return [Flow] of [UserDto] of the users that the user is following
     * @author Daneker
     * 12.07.2024
     */
    fun getUserFollowing(userId: String): Flow<UserDto>
}
