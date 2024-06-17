package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.model.entity.VerificationToken
import kz.danekerscode.coassembleapi.model.enums.VerificationTokenType
import kz.danekerscode.coassembleapi.repository.VerificationTokenRepository
import kz.danekerscode.coassembleapi.service.VerificationTokenService
import kz.danekerscode.coassembleapi.utils.Base64Utils
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Service
class VerificationTokenServiceImpl(
    private val verificationTokenRepository: VerificationTokenRepository,
    private val coAssembleProperties: CoAssembleProperties
) : VerificationTokenService {

    override fun revokeById(id: String): Mono<Void> = verificationTokenRepository.findById(id)
        .flatMap {
            it.enabled = false
            verificationTokenRepository.save(it)
        }
        .then()

    override fun findByValueAndUserEmail(
        value: String,
        userEmail: String,
        type: VerificationTokenType
    ): Mono<VerificationToken> {
        return verificationTokenRepository
            .findByValueAndUserEmailAndType(Base64Utils.decodeToString(value), userEmail, type)
    }

    override fun generateForUser(userEmail: String, type: VerificationTokenType): Mono<VerificationToken> {

        val verificationToken = VerificationToken(
            value = generateToken(),
            userEmail = userEmail,
            type = type,
            createdDate = LocalDateTime.now(),
            expireDate = LocalDateTime.now().plus(coAssembleProperties.verificationTokenTtl)
        )

        return verificationTokenRepository.save(verificationToken)
            .flatMap {
                it.value = Base64Utils.encodeToString(it.value)
                Mono.just(it)
            }
    }

    override fun revokeForUserByType(userEmail: String, type: VerificationTokenType): Mono<Void> =
        verificationTokenRepository.findAllByUserEmailAndType(userEmail, type)
            .flatMap {
                this.revokeById(it.id!!)
            }
            .then()

    private fun generateToken(): String = UUID.randomUUID().toString()

}