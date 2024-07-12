package kz.danekerscode.coassembleapi.features.techstackitem.domain.service.impl

import kotlinx.coroutines.flow.Flow
import kz.danekerscode.coassembleapi.features.techstackitem.data.entity.TechStackItem
import kz.danekerscode.coassembleapi.features.techstackitem.data.repository.TechStackItemRepository
import kz.danekerscode.coassembleapi.features.techstackitem.domain.service.TechStackItemService
import kz.danekerscode.coassembleapi.features.techstackitem.representation.dto.TechStackItemDto
import kz.danekerscode.coassembleapi.utils.copyToEntity
import kz.danekerscode.coassembleapi.utils.safeFindById
import kz.danekerscode.coassembleapi.utils.toEntity
import org.springframework.stereotype.Service

@Service
class TechStackItemServiceImpl(
    private val techStackItemRepository: TechStackItemRepository,
) : TechStackItemService {
    override suspend fun findAll(): Flow<TechStackItem> = techStackItemRepository.findAll()

    override suspend fun findById(id: String): TechStackItem = techStackItemRepository.safeFindById(id)

    override suspend fun deleteById(id: String) = techStackItemRepository.deleteById(id)

    override suspend fun save(techStackItem: TechStackItemDto): TechStackItem = techStackItemRepository.save(techStackItem.toEntity())

    override suspend fun update(
        id: String,
        techStackItem: TechStackItemDto,
    ): TechStackItem =
        this.findById(id)
            .apply {
                techStackItem.copyToEntity(this)
                techStackItemRepository.save(this)
            }
}
