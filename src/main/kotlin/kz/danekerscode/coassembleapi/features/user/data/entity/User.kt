package kz.danekerscode.coassembleapi.features.user.data.entity

import kz.danekerscode.coassembleapi.features.auth.data.enums.AuthType
import kz.danekerscode.coassembleapi.features.techstackitem.data.entity.TechStackItem
import kz.danekerscode.coassembleapi.features.user.data.enums.SecurityRole
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

@Document("users")
data class User(
    @Id
    val id: String? = null,
    var username: String,
    var email: String,
    var password: String? = null,
    var roles: List<SecurityRole>,
    var provider: AuthType,
    var emailVerified: Boolean = false,
    var image: Avatar? = null,
    @Field
    val techStack: List<TechStackItem> = mutableListOf(),
    var lastLoginAddress: String? = null, // todo check in each login
) : Serializable
