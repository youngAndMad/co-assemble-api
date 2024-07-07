package kz.danekerscode.coassembleapi.features.techstackitem.data.repository

import kz.danekerscode.coassembleapi.features.techstackitem.data.entity.TechStackItem
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TechStackItemRepository : CoroutineCrudRepository<TechStackItem, String>
