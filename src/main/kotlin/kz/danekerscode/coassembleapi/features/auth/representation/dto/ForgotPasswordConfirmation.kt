package kz.danekerscode.coassembleapi.features.auth.representation.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import kz.danekerscode.coassembleapi.core.annotation.Password

data class ForgotPasswordConfirmation(
    @field:Email
    val email: String,
    @field:NotBlank
    val token: String,
    @field:Password
    val password: String,
)
