package kz.danekerscode.coassembleapi.service

import kz.danekerscode.coassembleapi.model.entity.VerificationToken
import kz.danekerscode.coassembleapi.model.enums.VerificationTokenType
import reactor.core.publisher.Mono

interface VerificationTokenService {

    fun revokeById(id: String): Mono<Void>

    fun findByValueAndUserEmail(value: String, userEmail: String, type: VerificationTokenType): Mono<VerificationToken>

    fun generateForUser(userEmail: String, type: VerificationTokenType): Mono<VerificationToken>

    fun revokeForUserByType(userEmail: String, type: VerificationTokenType): Mono<Void>
}