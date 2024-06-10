package kz.danekerscode.coassembleapi.model.entity

import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.model.enums.SecurityRole
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

@Document("users")
class User(
    val id: String,
    var username: String,
    var email: String,
    var password: String,
    var roles: List<SecurityRole>,
    var provider: AuthType,
    var emailVerified: Boolean = false,
    @Field("techStack") // MongoDB field name
    val techStack: List<TechStackItem> = mutableListOf(),
    var lastLoginIp: String? = null, // todo check in each login
) : Serializable {
    constructor() : this("", "", "", "", emptyList(), AuthType.MANUAL) // todo delete
}
