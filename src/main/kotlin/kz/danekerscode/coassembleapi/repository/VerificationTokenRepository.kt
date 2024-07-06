package kz.danekerscode.coassembleapi.repository

import kotlinx.coroutines.flow.Flow
import kz.danekerscode.coassembleapi.model.entity.VerificationToken
import kz.danekerscode.coassembleapi.model.enums.VerificationTokenType
import org.springframework.stereotype.Repository

@Repository
interface VerificationTokenRepository : CoAssembleCoroutineMongoCrudRepository<VerificationToken, String> {

    suspend fun findByValueAndUserEmailAndType(
        value: String,
        userEmail: String,
        type: VerificationTokenType
    ): VerificationToken?

    suspend fun findAllByUserEmailAndType(userEmail: String, type: VerificationTokenType): Flow<VerificationToken>
}