package kz.danekerscode.coassembleapi.model.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import kz.danekerscode.coassembleapi.core.annotation.Password

data class RegistrationRequest(
    @field:NotBlank
    val username: String,
    @field:Password
    val password: String,
    @field:Email
    val email: String
)
