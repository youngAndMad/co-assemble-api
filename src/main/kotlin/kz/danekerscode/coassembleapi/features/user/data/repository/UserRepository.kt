package kz.danekerscode.coassembleapi.features.user.data.repository

import kz.danekerscode.coassembleapi.features.auth.data.enums.AuthType
import kz.danekerscode.coassembleapi.features.user.data.entity.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<User, String> {
    suspend fun findByEmail(email: String): User?

    suspend fun existsByEmailAndProvider(
        email: String,
        provider: AuthType,
    ): Boolean

}
