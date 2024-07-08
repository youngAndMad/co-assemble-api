package kz.danekerscode.coassembleapi.core.representation.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Id result")
data class IdResult(
    val id: String
)
