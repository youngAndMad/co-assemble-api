package kz.danekerscode.coassembleapi.utils

import org.springframework.core.ResolvableType

object GenericTypeResolver {

    fun resolveGenericClassAt(clazz: Class<*>?, parentClass: Class<*>?, idx: Int = 0): Class<*> {
        val resolvableType = ResolvableType.forClass(clazz).`as`(
            parentClass!!
        )
        return resolvableType.getGeneric(idx).resolve()!!
    }

}