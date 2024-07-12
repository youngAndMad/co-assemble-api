package kz.danekerscode.coassembleapi.features.project.domain.service.impl

import kz.danekerscode.coassembleapi.features.project.data.entity.Project
import kz.danekerscode.coassembleapi.features.project.data.entity.ProjectMember
import kz.danekerscode.coassembleapi.features.project.data.repository.ProjectMemberRepository
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectMemberService
import kz.danekerscode.coassembleapi.features.user.data.entity.User
import kz.danekerscode.coassembleapi.utils.safeDelete
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProjectMemberServiceImpl(
    private val projectMemberRepository: ProjectMemberRepository,
) : ProjectMemberService {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun deleteProjectMember(memberId: String) = projectMemberRepository.safeDelete(memberId)

    override suspend fun addProjectMember(
        user: User,
        project: Project,
    ) {
        ProjectMember(user = user, project = project).also {
            projectMemberRepository.save(it)
            log.info("Project member added: $it")
        }
    }
}
