package kz.danekerscode.coassembleapi.features.project.domain.service.impl

import kz.danekerscode.coassembleapi.features.project.data.repository.ProjectDurationRepository
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectDurationService
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProjectDurationServiceImpl(
    private val projectDurationRepository: ProjectDurationRepository,
    private val projectService: ProjectService
) : ProjectDurationService {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun toggleProjectDuration(projectId: String) {
        log.info("Toggling project duration for project with id: $projectId")

        val project = projectService.findProject(projectId)

        if (project.paused){

        }

    }

}