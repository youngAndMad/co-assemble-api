package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.model.entity.VerificationToken
import kz.danekerscode.coassembleapi.repository.VerificationTokenRepository
import kz.danekerscode.coassembleapi.service.VerificationTokenService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class VerificationTokenServiceImpl(
    private val verificationTokenRepository: VerificationTokenRepository
) : VerificationTokenService {

    override fun deleteById(id: String): Mono<Void> {
        return verificationTokenRepository.deleteById(id)
    }

    override fun findByValueAndUserEmail(value: String, userEmail: String): Mono<VerificationToken?> {
        return verificationTokenRepository.findByValueAndUserEmail(value, userEmail)
    }

    override fun save(verificationToken: VerificationToken): Mono<VerificationToken> {
        return verificationTokenRepository.save(verificationToken)
    }

    override fun cascadeForUser(userEmail: String): Mono<Void> {
        return verificationTokenRepository.deleteAllByUserEmail(userEmail)
    }

}