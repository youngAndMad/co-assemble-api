package kz.danekerscode.coassembleapi.model.entity

import kz.danekerscode.coassembleapi.model.enums.AuthType
import kz.danekerscode.coassembleapi.model.enums.SecurityRole
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import java.net.InetSocketAddress

@Document("users")
class User(
    @Id
    val id: String? = null,
    var username: String,
    var email: String,
    var password: String? = null,
    var roles: List<SecurityRole>,
    var provider: AuthType,
    var emailVerified: Boolean = false,
    var image: Avatar? = null,
    @Field("techStack") // MongoDB field name
    val techStack: List<TechStackItem> = mutableListOf(),
    var lastLoginAddress: InetSocketAddress? = null, // todo check in each login
) : Serializable {
}
