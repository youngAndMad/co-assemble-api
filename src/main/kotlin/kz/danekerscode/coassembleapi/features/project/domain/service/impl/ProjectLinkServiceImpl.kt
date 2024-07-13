package kz.danekerscode.coassembleapi.features.project.domain.service.impl

import kz.danekerscode.coassembleapi.core.representation.dto.IdResult
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.project.data.entity.Project
import kz.danekerscode.coassembleapi.features.project.data.entity.ProjectLink
import kz.danekerscode.coassembleapi.features.project.data.repository.ProjectLinkRepository
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectLinkService
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectService
import kz.danekerscode.coassembleapi.features.project.representation.dto.ProjectLinkRequest
import kz.danekerscode.coassembleapi.utils.safeFindById
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProjectLinkServiceImpl(
    private val projectLinkRepository: ProjectLinkRepository,
    private val projectService: ProjectService
) : ProjectLinkService {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun createProjectLink(
        projectLinkRequest: ProjectLinkRequest,
        projectId: String,
        currentUser: CoAssembleUserDetails
    ): IdResult {
        val project = projectService.findProject(projectId)
            .also { it.checkOwner(currentUser) }

        copyToProjectLink(projectLinkRequest, project).let {
            projectLinkRepository.save(it)
            log.info("Project link created: $it")
            return IdResult(it.id!!)
        }
    }

    override suspend fun updateProjectLink(
        projectLinkRequest: ProjectLinkRequest,
        projectLinkId: String,
        currentUser: CoAssembleUserDetails
    ) {
        projectLinkRepository.safeFindById(projectLinkId)
            .also { it.project.checkOwner(currentUser) }
            .let {
                it.type = projectLinkRequest.type
                it.link = projectLinkRequest.link
                it.description = projectLinkRequest.description

                projectLinkRepository.save(it)

                log.info("Project link updated: $it")
            }
    }


    override suspend fun deleteProjectLink(
        id: String
    ) {
        //todo check owner
        projectLinkRepository.deleteById(id)
        log.info("Project link deleted: $id")
    }


    private fun copyToProjectLink(
        projectLinkRequest: ProjectLinkRequest,
        project: Project
    ) = ProjectLink(
        type = projectLinkRequest.type,
        link = projectLinkRequest.link,
        description = projectLinkRequest.description,
        project = project
    )

}