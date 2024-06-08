package kz.danekerscode.coassembleapi.service

import kz.danekerscode.coassembleapi.model.entity.VerificationToken
import reactor.core.publisher.Mono

interface VerificationTokenService {

    fun deleteById(id: String): Mono<Void>

    fun findByValueAndUserEmail(value: String, userEmail: String): Mono<VerificationToken?>

    fun save(verificationToken: VerificationToken): Mono<VerificationToken>

    fun cascadeForUser(userEmail: String): Mono<Void>
}