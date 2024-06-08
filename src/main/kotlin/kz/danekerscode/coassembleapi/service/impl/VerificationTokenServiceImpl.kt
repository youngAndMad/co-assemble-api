package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.model.entity.VerificationToken
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

    override fun deleteById(id: String): Mono<Void> {
        return verificationTokenRepository.deleteById(id)
    }

    override fun findByValueAndUserEmail(value: String, userEmail: String): Mono<VerificationToken> {
        return verificationTokenRepository.findByValueAndUserEmail(Base64Utils.decodeToString(value), userEmail)
    }

    override fun cascadeForUser(userEmail: String): Mono<Void> {
        return verificationTokenRepository.deleteAllByUserEmail(userEmail)
    }

    override fun generateForUser(userEmail: String): Mono<VerificationToken> {

        val verificationToken = VerificationToken(
            value = generateToken(),
            userEmail = userEmail,
            createdDate = LocalDateTime.now(),
            expireDate = LocalDateTime.now().plus(coAssembleProperties.verificationTokenTtl)
        )

        return verificationTokenRepository.save(verificationToken)
            .flatMap {
                it.value = Base64Utils.encodeToString(it.value)
                Mono.just(it)
            }
    }

    private fun generateToken(): String = UUID.randomUUID().toString()

}