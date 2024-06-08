package kz.danekerscode.coassembleapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun githubWebClient() = WebClient.builder()
        .baseUrl("https://api.github.com")
        .build()

}