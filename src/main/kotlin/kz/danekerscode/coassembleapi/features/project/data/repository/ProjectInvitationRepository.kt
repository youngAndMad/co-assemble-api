package kz.danekerscode.coassembleapi.features.project.data.repository

import kz.danekerscode.coassembleapi.features.project.data.entity.ProjectInvitation
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProjectInvitationRepository : CoroutineCrudRepository<ProjectInvitation, String> {

    suspend fun findByProjectIdAndUserId(projectId: String, userId: String): ProjectInvitation?

}