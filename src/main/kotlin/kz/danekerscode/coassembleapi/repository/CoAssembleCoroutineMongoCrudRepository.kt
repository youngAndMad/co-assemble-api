package kz.danekerscode.coassembleapi.repository

import kz.danekerscode.coassembleapi.model.exception.EntityNotFoundException
import kz.danekerscode.coassembleapi.utils.GenericTypeResolver
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

@NoRepositoryBean
interface CoAssembleCoroutineMongoCrudRepository<T : Any, ID> : CoroutineCrudRepository<T, ID> {
    /**
     * A safe version of [CoroutineCrudRepository.findById] that throws an [EntityNotFoundException] if the entity is not found.
     * @param id the id of the entity to find
     * @return the entity with the given id
     * */
    suspend fun safeFindById(id: ID): T = findById(id) ?: throw EntityNotFoundException(domainClass(), id.toString())

    /**
     * A class that represents the domain class of the repository.
     * */
    fun domainClass() =
        GenericTypeResolver
            .resolveGenericClassAt(javaClass, CoAssembleCoroutineMongoCrudRepository::class.java)
}

