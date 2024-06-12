package kz.danekerscode.coassembleapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebConfig {

    @Bean
    fun githubWebClient() = WebClient.builder()
        .baseUrl("https://api.github.com")
        .build()

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val corsConfig = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:3000") // todo move to env
            maxAge = 8000L
            addAllowedMethod("*")
            addAllowedHeader("*")
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfig)
        }

        return CorsWebFilter(source)
    }
}