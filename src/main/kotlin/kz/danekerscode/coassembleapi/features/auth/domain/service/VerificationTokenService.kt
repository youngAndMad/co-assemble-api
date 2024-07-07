package kz.danekerscode.coassembleapi.features.auth.domain.service

import kz.danekerscode.coassembleapi.features.auth.data.entity.VerificationToken
import kz.danekerscode.coassembleapi.features.auth.data.enums.VerificationTokenType

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