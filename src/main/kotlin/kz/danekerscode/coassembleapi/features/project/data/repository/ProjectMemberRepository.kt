package kz.danekerscode.coassembleapi.features.project.data.repository

import kz.danekerscode.coassembleapi.features.project.data.entity.ProjectMember
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProjectMemberRepository : CoroutineCrudRepository<ProjectMember, String>
