package kz.danekerscode.coassembleapi

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import kz.danekerscode.coassembleapi.core.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.core.database.mongo.SoftDeleteMongoRepositoryFactoryBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories(repositoryFactoryBeanClass = SoftDeleteMongoRepositoryFactoryBean::class)
@OpenAPIDefinition(info = Info(title = "CoAssemble API"))
@EnableConfigurationProperties(CoAssembleProperties::class)
class CoAssembleApiApplication

fun main(args: Array<String>) {
    runApplication<CoAssembleApiApplication>(*args)
}
