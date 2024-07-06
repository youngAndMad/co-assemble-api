package kz.danekerscode.coassembleapi.repository
import kz.danekerscode.coassembleapi.model.exception.EntityNotFoundException
import org.springframework.core.ResolvableType
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

@NoRepositoryBean
interface CoAssembleCoroutineMongoCrudRepository<T : Any, ID> : CoroutineCrudRepository<T, ID> {

    suspend fun safeFindById(id: ID): T = findById(id) ?: throw EntityNotFoundException(domainClass(), id.toString())

    fun domainClass() =
        GenericTypeResolver
            .resolveGenericClassAt(javaClass, CoAssembleCoroutineMongoCrudRepository::class.java)
}

object GenericTypeResolver {

    internal fun resolveGenericClassAt(clazz: Class<*>?, parentClass: Class<*>?, idx: Int = 0): Class<*> {
        val resolvableType = ResolvableType.forClass(clazz).`as`(
            parentClass!!
        )
        return resolvableType.getGeneric(idx).resolve()!!
    }

}