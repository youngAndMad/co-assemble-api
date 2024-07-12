package kz.danekerscode.coassembleapi.features.project.data.entity

import kz.danekerscode.coassembleapi.core.data.entity.BaseEntity
import kz.danekerscode.coassembleapi.features.project.data.enums.ProjectInvitationStatus
import kz.danekerscode.coassembleapi.features.user.data.entity.User
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "project_invitations")
data class ProjectInvitation(
    var id: String? = null,
    @DBRef
    @ReadOnlyProperty
    var user: User,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var project: Project,
    var status: ProjectInvitationStatus = ProjectInvitationStatus.PENDING
) : BaseEntity()