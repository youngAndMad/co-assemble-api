package kz.danekerscode.coassembleapi.features.auth.data.repository

import kotlinx.coroutines.flow.Flow
import kz.danekerscode.coassembleapi.features.auth.data.entity.VerificationToken
import kz.danekerscode.coassembleapi.features.auth.data.enums.VerificationTokenType
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VerificationTokenRepository : CoroutineCrudRepository<VerificationToken, String> {

    suspend fun findByValueAndUserEmailAndType(
        value: String,
        userEmail: String,
        type: VerificationTokenType
    ): VerificationToken?

    suspend fun findAllByUserEmailAndType(userEmail: String, type: VerificationTokenType): Flow<VerificationToken>
}