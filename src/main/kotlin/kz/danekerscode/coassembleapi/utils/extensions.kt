package kz.danekerscode.coassembleapi.utils

import kz.danekerscode.coassembleapi.model.exception.EntityNotFoundException
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

suspend fun <T : Any, ID : Any> CoroutineCrudRepository<T, ID>.safeFindById(id: ID): T =  // todo move to separate file
    this.findById(id) ?: throw EntityNotFoundException(
        GenericTypeResolver.resolveGenericClassAt(
            this::class.java,
            MongoRepository::class.java
        ), id.toString()
    )
