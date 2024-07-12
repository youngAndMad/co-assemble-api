package kz.danekerscode.coassembleapi.core.domain.errors

import org.springframework.http.HttpStatus

class AuthProcessingException(override val message: String, val status: HttpStatus) : RuntimeException(
    "Error processing authentication request: $message",
)
