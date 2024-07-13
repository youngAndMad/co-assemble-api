package kz.danekerscode.coassembleapi.core.representation.dto

import io.swagger.v3.oas.annotations.media.Schema

@JvmInline
@Schema(description = "Id result")
value class IdResult(
    val id: String,
)
