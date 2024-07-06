package kz.danekerscode.coassembleapi

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import kz.danekerscode.coassembleapi.config.properties.CoAssembleProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "CoAssemble API"))
@EnableConfigurationProperties(CoAssembleProperties::class)
@EnableMongoRepositories(basePackages = ["kz.danekerscode.coassembleapi.repository"])
class CoAssembleApiApplication

fun main(args: Array<String>) {
    runApplication<CoAssembleApiApplication>(*args)
}

