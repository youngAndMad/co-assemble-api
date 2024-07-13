package kz.danekerscode.coassembleapi.features.project.domain.service

import kz.danekerscode.coassembleapi.core.representation.dto.IdResult
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.project.representation.dto.ProjectLinkRequest

interface ProjectLinkService {

    /**
     * Create a new project link
     * @param projectLinkRequest - request object
     * @param projectId - project id
     * @param currentUser - current user
     * @return IdResult - id of the created project link
     * @author Daneker
     * 13.07.2024
     */
    suspend fun createProjectLink(
        projectLinkRequest: ProjectLinkRequest,
        projectId: String,
        currentUser: CoAssembleUserDetails
    ): IdResult

    /**
     * Update project link
     * @param projectLinkRequest - request object
     * @param projectLinkId - project link id
     * @param currentUser - current user
     * @author Daneker
     * 13.07.2024
     */
    suspend fun updateProjectLink(
        projectLinkRequest: ProjectLinkRequest,
        projectLinkId: String,
        currentUser: CoAssembleUserDetails
    )

    /**
     * Delete project link
     * @param id - project link id
     * @author Daneker
     * 13.07.2024
     */
    suspend fun deleteProjectLink(id: String)

}