package kz.danekerscode.coassembleapi.features.user.domain.service.impl

import kz.danekerscode.coassembleapi.core.domain.errors.InvalidRequestPayloadException
import kz.danekerscode.coassembleapi.core.representation.dto.IdResult
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.user.data.entity.UserFollowing
import kz.danekerscode.coassembleapi.features.user.data.repository.UserFollowingRepository
import kz.danekerscode.coassembleapi.features.user.domain.service.UserFollowingService
import kz.danekerscode.coassembleapi.features.user.domain.service.UserService
import kz.danekerscode.coassembleapi.features.user.representation.dto.UserFollowingCount
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserFollowingServiceImpl(
    private val userFollowingRepository: UserFollowingRepository,
    private val userService: UserService,
) : UserFollowingService {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun followUser(
        currentUser: CoAssembleUserDetails,
        userId: String,
    ): IdResult {
        if (currentUser.user.id == userId) {
            throw InvalidRequestPayloadException("You can't follow himself")
        }

        if (userFollowingRepository.existsByRequesterIdAndAddresseeId(currentUser.user.id!!, userId)) {
            throw InvalidRequestPayloadException("You already followed this user")
        }

        userService.findById(userId).let {
            val userFollowing =
                UserFollowing(
                    addressee = it,
                    requester = currentUser.user,
                )

            userFollowingRepository.save(userFollowing)
                .apply {
                    log.info("User with id: ${currentUser.user.id} followed user with id: $userId")
                    return IdResult(id = id!!)
                    // todo send notification
                }
        }
    }

    override suspend fun unfollowUser(
        currentUser: CoAssembleUserDetails,
        userId: String,
    ) {
        if (!userFollowingRepository.existsByRequesterIdAndAddresseeId(currentUser.user.id!!, userId)) {
            throw InvalidRequestPayloadException("You didn't follow this user")
        }

        userFollowingRepository.deleteByRequesterIdAndAddresseeId(currentUser.user.id!!, userId)
            .apply {
                log.info("User with id: ${currentUser.user.id} unfollowed user with id: $userId")
            }
    }

    override suspend fun getUserFollowingCount(userId: String): UserFollowingCount =
        UserFollowingCount(
            followingCount = userFollowingRepository.countAllByRequesterId(userId),
            followersCount = userFollowingRepository.countAllByAddresseeId(userId),
        )
}
