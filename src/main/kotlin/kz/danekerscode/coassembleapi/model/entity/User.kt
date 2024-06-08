package kz.danekerscode.coassembleapi.model.entity

import kz.danekerscode.coassembleapi.model.SecurityRole
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val roles: List<SecurityRole>
)