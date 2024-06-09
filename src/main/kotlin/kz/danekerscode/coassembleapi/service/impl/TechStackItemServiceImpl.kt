package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.model.dto.user.TechStackItemDto
import kz.danekerscode.coassembleapi.model.entity.TechStackItem
import kz.danekerscode.coassembleapi.repository.TechStackItemRepository
import kz.danekerscode.coassembleapi.service.TechStackItemService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TechStackItemServiceImpl(
    private val techStackItemRepository: TechStackItemRepository
) : TechStackItemService {

    override fun findAll(): Flux<TechStackItem> = techStackItemRepository.findAll()

    override fun findById(id: String): Mono<TechStackItem> = techStackItemRepository.findById(id)

    override fun deleteById(id: String): Mono<Void> = techStackItemRepository.deleteById(id)

    override fun save(techStackItem: TechStackItemDto): Mono<TechStackItem> =
        techStackItemRepository.save(
            TechStackItem(
                name = techStackItem.name,
                description = techStackItem.description,
                type = techStackItem.type
            )
        )

    override fun update(id: String, techStackItem: TechStackItemDto): Mono<TechStackItem> =
        this.findById(id)
            .flatMap {
                it.apply {
                    name = techStackItem.name
                    description = techStackItem.description
                    type = techStackItem.type
                }
                techStackItemRepository.save(it)
            }
}