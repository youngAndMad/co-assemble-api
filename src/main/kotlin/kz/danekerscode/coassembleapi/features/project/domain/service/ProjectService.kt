package kz.danekerscode.coassembleapi.features.project.domain.service

import kz.danekerscode.coassembleapi.core.representation.dto.IdResult
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.project.data.entity.Project
import kz.danekerscode.coassembleapi.features.project.representation.dto.CreateProjectRequest

interface ProjectService {

    suspend fun createProject(
        createProjectRequest: CreateProjectRequest,
        currentUser: CoAssembleUserDetails
    ): IdResult

    suspend fun findProject(id: String): Project

    suspend fun toggleProjectFinished(project: Project)

    suspend fun deleteProject(id: String, currentUser: CoAssembleUserDetails)
}