package kz.danekerscode.coassembleapi.features.project.data.entity

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "project_durations")
data class ProjectDuration(
    var id: String? = null,
    @DBRef
    var project: Project,
    var start: LocalDate,
    var finish: LocalDate? = null
)