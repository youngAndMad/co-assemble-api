package kz.danekerscode.coassembleapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.session.ReactiveSessionRepository
import org.springframework.session.Session
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession
import org.springframework.session.data.mongo.ReactiveMongoSessionRepository


@Configuration
@EnableSpringWebSession
class SessionConfig {

    @Bean
    fun reactiveMongoSessionRepository(reactiveMongoTemplate: ReactiveMongoTemplate): ReactiveMongoSessionRepository =
        ReactiveMongoSessionRepository(reactiveMongoTemplate)
//
//    @Bean
//    fun webSessionManager(sessionStore: WebSessionStore): WebSessionManager {
//        val webSessionManager = DefaultWebSessionManager()
//        webSessionManager.sessionStore = sessionStore
//        return webSessionManager
//    }
//
//    @Bean
//    fun sessionStore(reactiveMongoTemplate: ReactiveMongoTemplate): WebSessionStore {
//        return SpringSessionWebSessionStore(ReactiveMongoSessionRepository(reactiveMongoTemplate))
//    }

}