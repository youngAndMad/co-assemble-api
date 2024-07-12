package kz.danekerscode.coassembleapi.features.project.domain.service

import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails

interface ProjectDurationService {
    suspend fun toggleProjectDuration(
        projectId: String,
        currentUser: CoAssembleUserDetails,
    )
}
