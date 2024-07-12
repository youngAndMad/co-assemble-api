package kz.danekerscode.coassembleapi.features.project.representation.rest

import io.swagger.v3.oas.annotations.tags.Tag
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectInvitationService
import kz.danekerscode.coassembleapi.features.project.representation.dto.ProjectMemberAction
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Project Invitations")
@RequestMapping("api/v1/project-invitations")
class ProjectInvitationController(
    private val projectInvitationService: ProjectInvitationService,
) {
    @PostMapping("/invite")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun inviteMember(
        @RequestBody actionPayload: ProjectMemberAction,
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails,
    ) = projectInvitationService.inviteMember(actionPayload, currentUser)

    @PostMapping("/accept/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun acceptInvitation(
        @PathVariable projectId: String,
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails,
    ) = projectInvitationService
        .acceptInvitation(projectId, currentUser)

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/reject/{projectId}")
    suspend fun rejectInvitation(
        @PathVariable projectId: String,
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails,
    ) = projectInvitationService.rejectInvitation(projectId, currentUser)

    @DeleteMapping("/cancel/{invitationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun cancelInvitation(
        @PathVariable invitationId: String,
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails,
    ) = projectInvitationService.cancelInvitation(invitationId, currentUser)
}
