package kz.danekerscode.coassembleapi.core.domain.errors

class EntityNotFoundException(message: String) : RuntimeException(message) {
    constructor(
        entityClass: Class<*>,
        id: Any
    ) : this("%s with id: %s not found".format(entityClass.simpleName, id.toString()))

    constructor(
        entityClass: Class<*>,
        vararg keyPair: Pair<String, *>
    ) : this(
        "%s with %s not found".format(
            entityClass.simpleName,
            keyPair.joinToString(", ") { (key, value) -> "$key: $value" }
        )
    )
}

