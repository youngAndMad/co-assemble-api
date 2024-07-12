package kz.danekerscode.coassembleapi.features.techstackitem.data.entity

import kz.danekerscode.coassembleapi.core.data.entity.BaseEntity
import kz.danekerscode.coassembleapi.features.techstackitem.data.enums.TechStackItemType
import org.springframework.data.mongodb.core.mapping.Document

@Document
class TechStackItem(
    val id: String? = null,
    var name: String,
    var description: String,
    var type: TechStackItemType
) : BaseEntity()