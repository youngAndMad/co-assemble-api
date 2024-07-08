package kz.danekerscode.coassembleapi.features.project.representation.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kz.danekerscode.coassembleapi.core.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.features.project.domain.service.ProjectService
import kz.danekerscode.coassembleapi.features.project.representation.dto.CreateProjectRequest
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Projects")
@RequestMapping("/api/v1/projects")
class ProjectController(
    private val projectService: ProjectService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new project")
    suspend fun createProject(
        @RequestBody createProjectRequest: CreateProjectRequest,
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails
    ) = projectService.createProject(createProjectRequest, currentUser)

    @GetMapping("{id}")
    @Operation(summary = "Find project by id")
    suspend fun findProject(@PathVariable id: String) = projectService.findProject(id)

//    suspend fun deleteProject(id: String) = projectService.deleteProject(id)
}