package kz.danekerscode.coassembleapi.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container


@Configuration
@EnableMongoRepositories
class MongoDBTestContainerConfig {

    companion object {
        private const val MONGO_PORT = 270171

        @Container
        var mongoDBContainer: MongoDBContainer = MongoDBContainer("mongo:latest")
            .withExposedPorts(MONGO_PORT)
    }

    init {
        mongoDBContainer.start();
        val mappedPort = mongoDBContainer.getMappedPort(MONGO_PORT);
        System.setProperty("mongodb.container.port", mappedPort.toString());
    }

}