package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.model.entity.VerificationToken
import kz.danekerscode.coassembleapi.model.enums.VerificationTokenType
import kz.danekerscode.coassembleapi.model.exception.EntityNotFoundException
import kz.danekerscode.coassembleapi.repository.VerificationTokenRepository
import kz.danekerscode.coassembleapi.service.VerificationTokenService
import kz.danekerscode.coassembleapi.utils.Base64Utils
import kz.danekerscode.coassembleapi.utils.safeFindById
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class VerificationTokenServiceImpl(
    private val verificationTokenRepository: VerificationTokenRepository,
    private val coAssembleProperties: CoAssembleProperties
) : VerificationTokenService {

    override suspend fun revokeById(id: String): Unit =
        verificationTokenRepository.safeFindById(id)
            .let {
                it.enabled = false
                save(it)
            }

    private suspend fun save(it: VerificationToken) = verificationTokenRepository.save(it)

    override suspend fun findByValueAndUserEmail(
        value: String,
        userEmail: String,
        type: VerificationTokenType
    ): VerificationToken {
        return verificationTokenRepository
            .findByValueAndUserEmailAndType(Base64Utils.decodeToString(value), userEmail, type)
            ?: throw EntityNotFoundException(
                VerificationToken::class.java,
                Pair("type", type),
                Pair("userEmail", userEmail),
                Pair("value", value)
            )
    }

    override suspend fun generateForUser(userEmail: String, type: VerificationTokenType): VerificationToken {
        val verificationToken = VerificationToken(
            value = generateToken(),
            userEmail = userEmail,
            type = type,
            createdDate = LocalDateTime.now(),
            expireDate = LocalDateTime.now().plus(coAssembleProperties.verificationTokenTtl)
        )

        return save(verificationToken)
            .apply {
                this.value = Base64Utils.encodeToString(this.value)
            }
    }

    override suspend fun revokeForUserByType(userEmail: String, type: VerificationTokenType) =
        verificationTokenRepository.findAllByUserEmailAndType(userEmail, type)
            .collect { it.id?.let { id -> this.revokeById(id) } }

    private fun generateToken(): String = UUID.randomUUID().toString()

}