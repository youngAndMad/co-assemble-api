package kz.danekerscode.coassembleapi.features.project.data.repository

import kz.danekerscode.coassembleapi.features.project.data.entity.ProjectLink
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProjectLinkRepository : CoroutineCrudRepository<ProjectLink, String> {
}