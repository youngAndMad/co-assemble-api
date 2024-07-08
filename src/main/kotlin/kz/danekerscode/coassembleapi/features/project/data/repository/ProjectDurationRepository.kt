package kz.danekerscode.coassembleapi.features.project.data.repository

import kz.danekerscode.coassembleapi.features.project.data.entity.ProjectDuration
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProjectDurationRepository: CoroutineCrudRepository<ProjectDuration, String> {
    
}