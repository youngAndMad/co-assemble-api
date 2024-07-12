package kz.danekerscode.coassembleapi.features.user.data.repository

import kotlinx.coroutines.flow.Flow
import kz.danekerscode.coassembleapi.features.user.data.entity.UserFollowing
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserFollowingRepository : CoroutineCrudRepository<UserFollowing, String> {
    /**
     * Check if the user is already following the user
     * @param requesterId
     * @param addresseeId
     * @return Boolean
     * @author Daneker
     * 12.07.2024
     */
    suspend fun existsByRequesterIdAndAddresseeId(
        requesterId: String,
        addresseeId: String,
    ): Boolean

    /**
     * Delete the user following by requesterId and addresseeId
     * @param id
     * @param userId
     * @return Unit
     * @author Daneker
     * 12.07.2024
     */
    suspend fun deleteByRequesterIdAndAddresseeId(
        id: String,
        userId: String,
    )

    /**
     * Count all by requesterId
     * @param requesterId
     * @return Int
     * @author Daneker
     * 12.07.2024
     */
    suspend fun countAllByRequesterId(requesterId: String): Int

    /**
     * Count all by addresseeId
     * @param addresseeId
     * @return Int
     * @author Daneker
     * 12.07.2024
     */
    suspend fun countAllByAddresseeId(addresseeId: String): Int

    /**
     * Find all by requesterId
     * @param requesterId
     * @return [Flow] of [UserFollowing]
     * @author Daneker
     * 12.07.2024
     */
    fun findAllByRequesterId(requesterId: String): Flow<UserFollowing>

    /**
     * Find all by addresseeId
     * @param addresseeId
     * @return [Flow] of [UserFollowing]
     * @author Daneker
     * 12.07.2024
     */
    fun findAllByAddresseeId(addresseeId: String): Flow<UserFollowing>
}
