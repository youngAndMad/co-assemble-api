package kz.danekerscode.coassembleapi.repository

import kz.danekerscode.coassembleapi.model.entity.TechStackItem
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TechStackItemRepository : MongoRepository<TechStackItem, String>
