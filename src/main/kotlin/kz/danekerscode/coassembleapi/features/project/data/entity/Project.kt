package kz.danekerscode.coassembleapi.features.project.data.entity

import kz.danekerscode.coassembleapi.core.data.entity.BaseEntity
import kz.danekerscode.coassembleapi.core.domain.errors.AuthProcessingException
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.user.data.entity.User
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import org.springframework.http.HttpStatus
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
) : BaseEntity() {
    /**
     * Check if the user is the owner of the project
     * @param userDetails - user details to check
     * @throws AuthProcessingException if the user is not the owner of the project
     * @author Daneker
     * 12.07.2024
     */
    fun checkOwner(userDetails: CoAssembleUserDetails) =
        if (userDetails.user.id != owner.id) {
            throw AuthProcessingException("You are not the owner of this project", HttpStatus.FORBIDDEN)
        } else {
            Unit
        }
}
