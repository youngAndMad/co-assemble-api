package kz.danekerscode.coassembleapi.model.entity

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class VerificationToken(
    val id: String? = null,
    val value: String,
    val createdDate: LocalDateTime,
    private val expireDate: LocalDateTime,
    val userEmail: String
) {
    fun isExpired(): Boolean {
        return expireDate.isBefore(LocalDateTime.now())
    }
}