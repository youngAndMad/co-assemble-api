package kz.danekerscode.coassembleapi.features.techstackitem.representation.dto

import jakarta.validation.constraints.NotBlank
import kz.danekerscode.coassembleapi.features.techstackitem.data.enums.TechStackItemType

data class TechStackItemDto(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val description: String,
    var type: TechStackItemType
)
