package kz.danekerscode.coassembleapi.features.project.domain.service.impl

import kz.danekerscode.coassembleapi.core.domain.errors.EntityNotFoundException
import kz.danekerscode.coassembleapi.core.representation.dto.IdResult
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.project.data.entity.ProjectInvitation
import kz.danekerscode.coassembleapi.features.project.data.enums.ProjectInvitationStatus
import kz.danekerscode.coassembleapi.features.project.data.repository.ProjectInvitationRepository
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectInvitationService
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectService
import kz.danekerscode.coassembleapi.features.project.representation.dto.ProjectMemberAction
import kz.danekerscode.coassembleapi.features.user.domain.service.UserService
import kz.danekerscode.coassembleapi.utils.safeFindById
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class ProjectInvitationServiceImpl(
    private val projectInvitationRepository: ProjectInvitationRepository,
    private val userService: UserService,
    private val projectService: ProjectService
) : ProjectInvitationService {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun inviteMember(
        actionPayload: ProjectMemberAction,
        currentUser: CoAssembleUserDetails
    ): IdResult {
        log.info("Inviting user ${actionPayload.userId} to project ${actionPayload.projectId}")
        val invitedUser = userService.findById(actionPayload.userId)
        val project = projectService.findProject(actionPayload.projectId)

        val projectInvitation = ProjectInvitation(
            project = project,
            user = invitedUser,
        )

        return save(projectInvitation).run {
            log.info("Invited user ${invitedUser.email} to project ${project.name}")
            // todo produce event for real time notification
            IdResult(id = id!!)
        }
    }

    override suspend fun acceptInvitation(
        projectId: String,
        currentUser: CoAssembleUserDetails
    ) {
        log.info("User ${currentUser.user.email} accepted invitation to project $projectId")

        getProjectInvitation(projectId, currentUser).let {
            it.status = ProjectInvitationStatus.ACCEPTED
            projectInvitationRepository.save(it)

            log.info("User ${currentUser.user.email} accepted invitation to project $projectId")
        }
        // todo send notification to project owner about new member
    }

    override suspend fun rejectInvitation(
        projectId: String,
        currentUser: CoAssembleUserDetails
    ) {
        log.info("User ${currentUser.user.email} rejected invitation to project $projectId")


        getProjectInvitation(projectId, currentUser).let {
            it.status = ProjectInvitationStatus.REJECTED
            save(it)

            log.info("User ${currentUser.user.email} rejected invitation to project $projectId")
        }
    }

    private suspend fun save(it: ProjectInvitation) = projectInvitationRepository.save(it)

    override suspend fun cancelInvitation(
        invitationId: String,
        currentUser: CoAssembleUserDetails
    ) {
        log.info("User ${currentUser.user.email} canceled invitation $invitationId")

        projectInvitationRepository.safeFindById(invitationId).let {
            it.status = ProjectInvitationStatus.CANCELED
            save(it)
            log.info("User ${currentUser.user.email} canceled invitation $invitationId")
        }
    }

    private suspend fun getProjectInvitation(
        projectId: String,
        currentUser: CoAssembleUserDetails
    ) = projectInvitationRepository
        .findByProjectIdAndUserId(projectId, currentUser.user.id!!) ?: throw EntityNotFoundException(
        ProjectInvitation::class.java, Pair("projectId", projectId), Pair(
            "userId",
            currentUser.user.id
        )
    )


}