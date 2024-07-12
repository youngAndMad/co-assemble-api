package kz.danekerscode.coassembleapi

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kz.danekerscode.coassembleapi.core.config.properties.CoAssembleProperties
import kz.danekerscode.coassembleapi.core.database.mongo.SoftDeleteMongoRepositoryFactoryBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "CoAssemble API"))
@EnableConfigurationProperties(CoAssembleProperties::class)
class CoAssembleApiApplication {
    @Configuration
    class Test { // todo move to separate config class
        @Bean
        fun applicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    fun main(args: Array<String>) {
        runApplication<CoAssembleApiApplication>(*args)
    }
}


