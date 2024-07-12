package kz.danekerscode.coassembleapi.features.project.representation.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectDurationService
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectService
import kz.danekerscode.coassembleapi.features.project.representation.dto.CreateProjectRequest
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

/**
 * Controller for handling project related endpoints
 * @property projectService - service for handling project related operations
 * @property projectDurationService - service for handling project duration related operations
 * @author Daneker
 * 12.07.2024
 */
@RestController
@Tag(name = "Projects")
@RequestMapping("/api/v1/projects")
class ProjectController(
    private val projectService: ProjectService,
    private val projectDurationService: ProjectDurationService
) {

    /**
     * Endpoint for creating a new project
     * @param createProjectRequest - request body for creating a new project
     * @param currentUser - current user details: Will be injected by Spring Security
     * @author Daneker
     * 12.07.2024
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new project")
    suspend fun createProject(
        @RequestBody createProjectRequest: CreateProjectRequest,
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails
    ) = projectService.createProject(createProjectRequest, currentUser)

    /**
     * Endpoint for finding a project by id
     * @param id - id of the project to find
     * @return [kz.danekerscode.coassembleapi.features.project.data.entity.Project] - the project found by the id
     * @author Daneker
     * 12.07.2024
     */
    @GetMapping("{id}")
    @Operation(summary = "Find project by id")
    suspend fun findProject(@PathVariable id: String) = projectService.findProject(id)

    /**
     * Endpoint for toggling the status of a project
     * @param id - id of the project to toggle
     * @param currentUser - current user details: Will be injected by Spring Security
     * @author Daneker
     * 12.07.2024
     */
    @PatchMapping("{id}/status")
    @Operation(summary = "Toggle project status")
    suspend fun toggleProjectStatus(
        @PathVariable id: String,
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails
    ) = projectDurationService.toggleProjectDuration(id, currentUser)

    /**
     * Endpoint for deleting a project
     * @param id - id of the project to delete
     * @param currentUser - current user details: Will be injected by Spring Security
     * @author Daneker
     * 12.07.2024
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete project")
    suspend fun deleteProject(
        @PathVariable id: String,
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails
    ) =
        projectService.deleteProject(id, currentUser)
}