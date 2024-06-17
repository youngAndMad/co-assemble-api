package kz.danekerscode.coassembleapi.model.entity

import kz.danekerscode.coassembleapi.model.enums.VerificationTokenType
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class VerificationToken(
    val id: String? = null,
    var value: String,
    val createdDate: LocalDateTime,
    val type: VerificationTokenType,
    private val expireDate: LocalDateTime,
    val userEmail: String,
    val used: Boolean = false,
    var enabled: Boolean = true
) {
    fun isExpired(): Boolean {
        return expireDate.isBefore(LocalDateTime.now())
    }
}