package kz.danekerscode.coassembleapi.core.config

import kz.danekerscode.coassembleapi.core.database.mongo.SoftDeleteMongoRepositoryFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.time.LocalDateTime
import java.util.*

@Configuration
@EnableMongoRepositories(repositoryFactoryBeanClass = SoftDeleteMongoRepositoryFactoryBean::class)
@EnableMongoAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
class MongoConfig {

    /**
     * With this configuration in place, we can now enable [@CreatedDate] and [@LastModifiedDate] on our documents
     * We also need a @Version field in our documents, otherwise there will be problems in combination with the Id field.
     * If the id is pre-filled by our application, auditing assumes that the document already existed in the database and
     * [@CreatedDate] is not set. With the version field however, the life cycle of our entity is correctly captured.
     * @author Daneker
     * 12.07.2024
     */
    @Bean(name = ["auditingDateTimeProvider"])
    fun dateTimeProvider(): DateTimeProvider = DateTimeProvider { Optional.of(LocalDateTime.now()) }


}