package kz.danekerscode.coassembleapi.service

import kz.danekerscode.coassembleapi.model.dto.user.TechStackItemDto
import kz.danekerscode.coassembleapi.model.entity.TechStackItem
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TechStackItemService {

    fun findAll(): Flux<TechStackItem>

    fun findById(id: String): Mono<TechStackItem>

    fun deleteById(id: String): Mono<Void>

    fun save(techStackItem: TechStackItemDto): Mono<TechStackItem>

    fun update(id: String, techStackItem: TechStackItemDto): Mono<TechStackItem>
}