package kz.danekerscode.coassembleapi.repository

import kz.danekerscode.coassembleapi.model.entity.User
import kz.danekerscode.coassembleapi.model.enums.AuthType
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveMongoRepository<User, String> {

    fun findByEmail(email: String): Mono<User>

    fun existsByEmailAndProvider(email: String, provider: AuthType): Mono<Boolean>
}