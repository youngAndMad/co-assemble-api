package kz.danekerscode.coassembleapi.repository

import kz.danekerscode.coassembleapi.model.entity.TechStackItem
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TechStackItemRepository : ReactiveMongoRepository<TechStackItem, String>
