package kz.danekerscode.coassembleapi.repository

import kz.danekerscode.coassembleapi.model.entity.VerificationToken
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface VerificationTokenRepository : ReactiveMongoRepository<VerificationToken, String> {

    fun findByValueAndUserEmail(value: String, userEmail: String): Mono<VerificationToken>

    fun deleteAllByUserEmail(userEmail: String): Mono<Void>
}