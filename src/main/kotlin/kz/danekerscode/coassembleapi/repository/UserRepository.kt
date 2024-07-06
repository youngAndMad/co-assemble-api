package kz.danekerscode.coassembleapi.repository

import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String> {

    suspend fun findByEmail(email: String): User?

    suspend fun existsByEmailAndProvider(email: String, provider: AuthType): Boolean

}