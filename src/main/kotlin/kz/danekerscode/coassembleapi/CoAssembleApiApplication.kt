package kz.danekerscode.coassembleapi

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import kz.danekerscode.coassembleapi.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.model.exception.EntityNotFoundException
import kz.danekerscode.coassembleapi.utils.GenericTypeResolver
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "CoAssemble API"))
@EnableConfigurationProperties(CoAssembleProperties::class)
@EnableMongoRepositories(basePackages = ["kz.danekerscode.coassembleapi.repository"])
class CoAssembleApiApplication

fun main(args: Array<String>) {
    runApplication<CoAssembleApiApplication>(*args)
}

fun <T : Any, ID : Any> MongoRepository<T, ID>.safeFindById(id: ID): T { // todo move to separate file
    val entity = this.findById(id)
    return entity.orElseThrow {
        EntityNotFoundException(
            GenericTypeResolver.resolveGenericClassAt(
                this::class.java,
                MongoRepository::class.java
            ), id.toString()
        )
    }
}