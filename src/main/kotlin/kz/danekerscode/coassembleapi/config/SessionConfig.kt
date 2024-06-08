package kz.danekerscode.coassembleapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession
import org.springframework.session.data.mongo.ReactiveMongoSessionRepository


@Configuration
@EnableSpringWebSession
class SessionConfig {

    @Bean
    fun reactiveMongoSessionRepository(reactiveMongoTemplate: ReactiveMongoTemplate): ReactiveMongoSessionRepository =
        ReactiveMongoSessionRepository(reactiveMongoTemplate)

}