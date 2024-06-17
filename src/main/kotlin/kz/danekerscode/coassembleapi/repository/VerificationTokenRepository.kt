package kz.danekerscode.coassembleapi.repository

import kz.danekerscode.coassembleapi.model.entity.VerificationToken
import kz.danekerscode.coassembleapi.model.enums.VerificationTokenType
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface VerificationTokenRepository : ReactiveMongoRepository<VerificationToken, String> {

    fun findByValueAndUserEmailAndType(
        value: String,
        userEmail: String,
        type: VerificationTokenType
    ): Mono<VerificationToken>

    fun findAllByUserEmailAndType(userEmail: String, type: VerificationTokenType): Flux<VerificationToken>
}