package kz.danekerscode.coassembleapi.features.project.data.repository

import kz.danekerscode.coassembleapi.features.project.data.entity.Project
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProjectRepository : CoroutineCrudRepository<Project, String>
