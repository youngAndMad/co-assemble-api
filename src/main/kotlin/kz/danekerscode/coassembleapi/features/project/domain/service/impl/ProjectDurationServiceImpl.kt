package kz.danekerscode.coassembleapi.features.project.domain.service.impl

import kz.danekerscode.coassembleapi.features.project.data.entity.Project
import kz.danekerscode.coassembleapi.features.project.data.entity.ProjectDuration
import kz.danekerscode.coassembleapi.features.project.data.repository.ProjectDurationRepository
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectDurationService
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ProjectDurationServiceImpl(
    private val projectDurationRepository: ProjectDurationRepository,
    private val projectService: ProjectService
) : ProjectDurationService {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun toggleProjectDuration(projectId: String) {
        log.info("Toggling project duration for project with id: $projectId")

        projectService.findProject(projectId)
            .let {
                if (!it.paused) {
                    pauseProject(it)
                } else {
                    createNewProjectDuration(it)
                }
                projectService.toggleProjectFinished(it)
            }
    }

    private suspend fun pauseProject(
        project: Project
    ) = project.durations.last().let {
        it.finish = LocalDate.now()
        projectDurationRepository.save(it)
    }.also { log.info("Paused project ${project.id} ") }

    private suspend fun createNewProjectDuration(
        project: Project
    ) = ProjectDuration(project = project, start = LocalDate.now()).let {
        projectDurationRepository.save(it)
    }.run { log.info("Resumed project ${project.id} ") }

}