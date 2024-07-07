package kz.danekerscode.coassembleapi.features.project.data.entity

import kz.danekerscode.coassembleapi.features.user.data.entity.User
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "project_members")
data class ProjectMember(
    var id: String? = null,
    @DBRef
    var user: User,
    @DBRef
    var project: Project,
    var joinedAt: LocalDateTime = LocalDateTime.now(),
    var participationDescription: String? = null
)
