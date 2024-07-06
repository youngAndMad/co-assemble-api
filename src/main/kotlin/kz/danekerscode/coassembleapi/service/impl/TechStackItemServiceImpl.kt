package kz.danekerscode.coassembleapi.service.impl

import kotlinx.coroutines.flow.Flow
import kz.danekerscode.coassembleapi.model.dto.user.TechStackItemDto
import kz.danekerscode.coassembleapi.model.entity.TechStackItem
import kz.danekerscode.coassembleapi.repository.TechStackItemRepository
import kz.danekerscode.coassembleapi.service.TechStackItemService
import org.springframework.stereotype.Service

@Service
class TechStackItemServiceImpl(
    private val techStackItemRepository: TechStackItemRepository
) : TechStackItemService {

    override suspend fun findAll(): Flow<TechStackItem> = techStackItemRepository.findAll()

    override suspend fun findById(id: String): TechStackItem = techStackItemRepository.safeFindById(id)

    override suspend fun deleteById(id: String) = techStackItemRepository.deleteById(id)

    override suspend fun save(techStackItem: TechStackItemDto): TechStackItem =
        techStackItemRepository.save(
            TechStackItem(
                name = techStackItem.name,
                description = techStackItem.description,
                type = techStackItem.type
            )
        )

    override suspend fun update(id: String, techStackItem: TechStackItemDto): TechStackItem =
        this.findById(id)
            .apply {
                name = techStackItem.name // todo create a separate fun to copy properties
                description = techStackItem.description
                type = techStackItem.type
                techStackItemRepository.save(this)
            }
}