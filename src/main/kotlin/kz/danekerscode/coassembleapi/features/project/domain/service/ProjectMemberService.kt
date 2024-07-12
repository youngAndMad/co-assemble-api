package kz.danekerscode.coassembleapi.features.project.domain.service

import kz.danekerscode.coassembleapi.features.project.data.entity.Project
import kz.danekerscode.coassembleapi.features.user.data.entity.User

interface ProjectMemberService {
    suspend fun deleteProjectMember(memberId: String)

    suspend fun addProjectMember(
        user: User,
        project: Project,
    )
}
