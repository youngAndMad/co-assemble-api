package kz.danekerscode.coassembleapi.model.entity

import kz.danekerscode.coassembleapi.model.enums.TechStackItemType
import org.springframework.data.mongodb.core.mapping.Document

@Document
class TechStackItem(
    val id: String? = null,
    var name: String,
    var description: String,
    var type: TechStackItemType
)