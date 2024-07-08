package kz.danekerscode.coassembleapi.features.project.data.entity

import kz.danekerscode.coassembleapi.features.user.data.entity.User
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import java.time.LocalDateTime

@Document(collection = "projects")
data class Project(
    var id: String? = null,
    var name: String,
    var goal: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var paused: Boolean = false,
    @ReadOnlyProperty
    @DBRef
    val owner: User,
    @ReadOnlyProperty
    @DocumentReference
    var durations: MutableList<ProjectDuration> = mutableListOf(),
    @DocumentReference
    @ReadOnlyProperty
    var members: MutableList<ProjectMember> = mutableListOf(),
)