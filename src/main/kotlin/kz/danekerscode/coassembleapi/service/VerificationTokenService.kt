package kz.danekerscode.coassembleapi.service

import kz.danekerscode.coassembleapi.model.entity.VerificationToken
import kz.danekerscode.coassembleapi.model.enums.VerificationTokenType

interface VerificationTokenService {

    suspend fun revokeById(id: String)

    suspend fun findByValueAndUserEmail(
        value: String,
        userEmail: String,
        type: VerificationTokenType
    ): VerificationToken

    suspend fun generateForUser(userEmail: String, type: VerificationTokenType): VerificationToken

    suspend fun revokeForUserByType(userEmail: String, type: VerificationTokenType)
}