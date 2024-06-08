package kz.danekerscode.coassembleapi.model.dto.auth

import jakarta.validation.constraints.Email
import kz.danekerscode.coassembleapi.core.annotation.Password

data class LoginRequest(
    @field:Email
    val email: String,
    @field:Password
    val password: String
)
