package kz.danekerscode.coassembleapi.features.project.data.entity

import kz.danekerscode.coassembleapi.core.data.entity.BaseEntity
import kz.danekerscode.coassembleapi.features.project.data.enums.ProjectLinkType
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.net.URL

/**
 * Entity for project link
 * @param type - type of the link
 * @param link - URL of the link
 * @param description - description of the link
 * @author Daneker
 * 13.07.2024
 */
@Document(collection = "project_links")
data class ProjectLink(
    @Id
    var id: String? = null,
    var type: ProjectLinkType,
    var link: URL,
    var description: String,
    @DBRef(lazy = true)
    @ReadOnlyProperty
    var project: Project,
) : BaseEntity()