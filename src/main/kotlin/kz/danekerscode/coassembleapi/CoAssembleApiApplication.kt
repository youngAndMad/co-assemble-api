package kz.danekerscode.coassembleapi

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import kz.danekerscode.coassembleapi.config.properties.CoAssembleProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = io.swagger.v3.oas.annotations.info.Info(title = "CoAssemble API"))
@EnableConfigurationProperties(CoAssembleProperties::class)
class CoAssembleApiApplication

fun main(args: Array<String>) {
    runApplication<CoAssembleApiApplication>(*args)
}
