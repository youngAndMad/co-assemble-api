package kz.danekerscode.coassembleapi.model.dto.user

import jakarta.validation.constraints.NotBlank
import kz.danekerscode.coassembleapi.model.enums.TechStackItemType

data class TechStackItemDto(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val description: String,
    var type: TechStackItemType
)
