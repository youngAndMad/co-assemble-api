package kz.danekerscode.coassembleapi.features.project.domain.service

interface ProjectDurationService {

    suspend fun toggleProjectDuration(projectId: String)

}