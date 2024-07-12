package kz.danekerscode.coassembleapi.features.user.representation.dto

/**
 * Data Transfer Object for UserFollowingCount
 * @property followingCount Int -> Number of users that the user is following
 * @property followersCount Int -> Number of users that are following the user
 * @author Daneker
 * 12.07.2024
 */
data class UserFollowingCount(
    val followingCount: Int,
    val followersCount: Int,
)
