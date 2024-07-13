package kz.danekerscode.coassembleapi.features.project.representation.rest

import io.swagger.v3.oas.annotations.Operation
import kz.danekerscode.coassembleapi.core.representation.dto.IdResult
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectLinkService
import kz.danekerscode.coassembleapi.features.project.representation.dto.ProjectLinkRequest
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/projects/links")
class ProjectLinkController(
    private val projectLinkService: ProjectLinkService
) {

    /**
     * Endpoint for creating a new project link
     * @param projectId - project id
     * @param projectLinkRequest - request object
     * @param currentUser - current user. Will be injected by Spring Security
     * @return [IdResult] - id of the created project link
     * @author Daneker
     * 13.07.2024
     */
    @Operation(summary = "Create a new project link")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{projectId}")
    suspend fun createProjectLink(
        @PathVariable projectId: String,
        @RequestBody projectLinkRequest: ProjectLinkRequest,
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails
    ): IdResult = projectLinkService.createProjectLink(projectLinkRequest, projectId, currentUser)

    /**
     * Endpoint for updating a project link
     * @param projectLinkId - project link id
     * @param projectLinkRequest - request object
     * @param currentUser - current user. Will be injected by Spring Security
     * @author Daneker
     * 13.07.2024
     */
    @Operation(summary = "Update project link")
    @PutMapping("/{projectLinkId}")
    suspend fun updateProjectLink(
        @RequestBody projectLinkRequest: ProjectLinkRequest,
        @PathVariable projectLinkId: String,
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails
    ) = projectLinkService.updateProjectLink(projectLinkRequest, projectLinkId, currentUser)

    /**
     * Endpoint for deleting a project link
     * @param projectLinkId - project link id
     * @author Daneker
     * 13.07.2024
     */
    @Operation(summary = "Update project link")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{projectLinkId}")
    suspend fun deleteProject(
        @PathVariable projectLinkId: String
    ) = projectLinkService.deleteProjectLink(projectLinkId)
}