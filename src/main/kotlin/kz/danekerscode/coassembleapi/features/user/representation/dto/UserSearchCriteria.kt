package kz.danekerscode.coassembleapi.features.user.representation.dto

import kz.danekerscode.coassembleapi.features.techstackitem.data.enums.TechStackItemType

data class UserSearchCriteria(
    val keyword: String? = null,
    val stackItemType: TechStackItemType? = null,
)
