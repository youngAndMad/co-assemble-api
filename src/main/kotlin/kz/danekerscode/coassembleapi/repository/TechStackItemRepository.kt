package kz.danekerscode.coassembleapi.repository

import kz.danekerscode.coassembleapi.model.entity.TechStackItem
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TechStackItemRepository : CoroutineCrudRepository<TechStackItem, String>
