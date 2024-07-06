package kz.danekerscode.coassembleapi.utils

import com.mongodb.BasicDBObject
import kz.danekerscode.coassembleapi.model.dto.user.TechStackItemDto
import kz.danekerscode.coassembleapi.model.entity.TechStackItem
import kz.danekerscode.coassembleapi.model.exception.EntityNotFoundException
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.web.multipart.MultipartFile

suspend fun <T : Any, ID : Any> CoroutineCrudRepository<T, ID>.safeFindById(id: ID): T =
    this.findById(id) ?: throw EntityNotFoundException(
        GenericTypeResolver.resolveGenericClassAt(
            this::class.java,
            MongoRepository::class.java
        ), id.toString()
    )

fun MultipartFile.basicDbObject(): BasicDBObject {
    val it = this
    return BasicDBObject().apply {
        put("type", "file")
        put("size", it.size)
        put("name", it.name)
        put("extension", it.contentType ?: "")
    }
}

fun TechStackItemDto.toEntity() = TechStackItem(
    name = this.name,
    description = this.description,
    type = this.type
)

fun TechStackItemDto.copyToEntity(item: TechStackItem): Unit {
    val it = this
    item.apply {
        this.name = it.name
        this.description = it.description
        this.type = it.type
    }
}