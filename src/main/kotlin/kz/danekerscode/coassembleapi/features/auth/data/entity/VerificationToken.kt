package kz.danekerscode.coassembleapi.features.auth.data.entity

import kz.danekerscode.coassembleapi.core.data.entity.BaseEntity
import kz.danekerscode.coassembleapi.core.domain.errors.AuthProcessingException
import kz.danekerscode.coassembleapi.features.auth.data.enums.VerificationTokenType
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@Document
class VerificationToken(
    val id: String? = null,
    var value: String,
    val type: VerificationTokenType,
    private val expireDate: LocalDateTime,
    val userEmail: String,
    val used: Boolean = false,
    var enabled: Boolean = true
) : BaseEntity() {
    private fun isExpired(): Boolean {
        return expireDate.isBefore(LocalDateTime.now())
    }

    fun checkValidation() {
        if (isExpired() || !enabled) {
            throw AuthProcessingException(
                "Verification token expired",
                HttpStatus.BAD_REQUEST
            )
        }
    }
}