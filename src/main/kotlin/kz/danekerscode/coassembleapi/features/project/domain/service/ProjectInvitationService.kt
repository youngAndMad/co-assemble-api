package kz.danekerscode.coassembleapi.features.project.domain.service

import kz.danekerscode.coassembleapi.core.representation.dto.IdResult
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.project.representation.dto.ProjectMemberAction

interface ProjectInvitationService {

    suspend fun inviteMember(
        actionPayload: ProjectMemberAction,
        currentUser: CoAssembleUserDetails
    ): IdResult

    suspend fun acceptInvitation(
        projectId: String,
        currentUser: CoAssembleUserDetails
    )

    suspend fun rejectInvitation(
        projectId: String,
        currentUser: CoAssembleUserDetails
    )

    suspend fun cancelInvitation(
        invitationId: String,
        currentUser: CoAssembleUserDetails
    )

}