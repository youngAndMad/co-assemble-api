package kz.danekerscode.coassembleapi.model.dto.user

import kz.danekerscode.coassembleapi.model.enums.TechStackItemType

data class UserSearchCriteria(
    val keyword: String? = null,
    val stackItemType: TechStackItemType? = null,

)
