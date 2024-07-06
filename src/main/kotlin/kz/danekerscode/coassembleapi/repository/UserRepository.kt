package kz.danekerscode.coassembleapi.repository

import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoAssembleCoroutineMongoCrudRepository<User, String> {

    suspend fun findByEmail(email: String): User?

    suspend fun existsByEmailAndProvider(email: String, provider: AuthType): Boolean

}