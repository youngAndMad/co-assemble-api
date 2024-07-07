package kz.danekerscode.coassembleapi.features.project.domain.service.impl

import kz.danekerscode.coassembleapi.core.representation.dto.IdResult
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.project.data.entity.Project
import kz.danekerscode.coassembleapi.features.project.data.repository.ProjectRepository
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectService
import kz.danekerscode.coassembleapi.features.project.representation.dto.CreateProjectRequest
import kz.danekerscode.coassembleapi.features.user.domain.service.UserService
import kz.danekerscode.coassembleapi.utils.safeFindById
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class ProjectServiceImpl(
    private val projectRepository: ProjectRepository,
    private val eventBus: ApplicationEventPublisher,
    private val userService: UserService
) : ProjectService {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun createProject(
        createProjectRequest: CreateProjectRequest,
        currentUser: CoAssembleUserDetails
    ): IdResult {
        val project = Project(
            owner = currentUser.user,
            goal = createProjectRequest.goal,
            name = createProjectRequest.name,
        )

        return projectRepository.save(project).run {
            log.info("Created new project by ${currentUser.user.email} with id $id")
            IdResult(id = id!!)
        }
    }

    override suspend fun findProject(id: String) = projectRepository.safeFindById(id)
}