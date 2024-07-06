package kz.danekerscode.coassembleapi.repository

import kz.danekerscode.coassembleapi.model.entity.TechStackItem
import org.springframework.stereotype.Repository

@Repository
interface TechStackItemRepository : CoAssembleCoroutineMongoCrudRepository<TechStackItem, String>
