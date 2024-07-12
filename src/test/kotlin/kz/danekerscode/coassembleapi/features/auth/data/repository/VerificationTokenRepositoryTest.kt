package kz.danekerscode.coassembleapi.features.auth.data.repository

import kz.danekerscode.coassembleapi.config.MongoDBTestContainerConfig
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers

@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = [MongoDBTestContainerConfig::class])
class VerificationTokenRepositoryTest {
}