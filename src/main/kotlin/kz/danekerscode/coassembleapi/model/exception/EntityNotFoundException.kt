package kz.danekerscode.coassembleapi.model.exception

class EntityNotFoundException(entityClass: Class<*>, id: Any) :
    RuntimeException("%s with id: %s not found".format(entityClass.simpleName, id.toString()))