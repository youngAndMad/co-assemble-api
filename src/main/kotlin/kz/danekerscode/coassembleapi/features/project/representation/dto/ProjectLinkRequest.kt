package kz.danekerscode.coassembleapi.features.project.representation.dto

import kz.danekerscode.coassembleapi.features.project.data.enums.ProjectLinkType
import java.net.URL

/**
 * DTO for create,  update project link
 * @param type - type of the link
 * @param link - URL of the link
 * @param description - description of the link
 * @author Daneker
 * 13.07.2024
 */

data class ProjectLinkRequest(
    var type: ProjectLinkType,
    var link: URL,
    var description: String,
)
