package kz.danekerscode.coassembleapi.service

import kotlinx.coroutines.flow.Flow
import kz.danekerscode.coassembleapi.model.dto.user.TechStackItemDto
import kz.danekerscode.coassembleapi.model.entity.TechStackItem

interface TechStackItemService {

    suspend fun findAll(): Flow<TechStackItem>

    suspend fun findById(id: String): TechStackItem

    suspend fun deleteById(id: String)

    suspend fun save(techStackItem: TechStackItemDto): TechStackItem

    suspend fun update(id: String, techStackItem: TechStackItemDto): TechStackItem
}