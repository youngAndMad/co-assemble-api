package kz.danekerscode.coassembleapi.features.project.representation.dto

/**
 * DTO for updating a project
 * @property name title of the project
 * @property goal goal of the project
 * @author Daneker
 * 12.07.2024
 */
data class UpdateProjectRequest(
    val name: String,
    val goal: String,
)
