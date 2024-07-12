package kz.danekerscode.coassembleapi.features.user.data.entity

import kz.danekerscode.coassembleapi.core.data.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user_followings")
data class UserFollowing(
    @Id
    val id: String? = null,
    @DBRef(lazy = true)
    @ReadOnlyProperty
    val addressee: User,
    @DBRef(lazy = true)
    @ReadOnlyProperty
    val requester: User,
//    override var createdDate: LocalDateTime? = LocalDateTime.now(),
) : BaseEntity()
