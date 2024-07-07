package kz.danekerscode.coassembleapi.core.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class WebConfig {

    @Value("\${cors.allowed.origins}")
    private lateinit var allowedOriginList: List<String>

    @Bean
    fun githubWebClient() = RestClient.builder()
        .baseUrl("https://api.github.com")
        .build()

    @Bean
    fun corsWebFilter(): CorsFilter {
        val corsConfig = CorsConfiguration().apply {
            allowedOrigins = allowedOriginList
            maxAge = 8000L
            addAllowedMethod("*")
            addAllowedHeader("*")
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfig)
        }

        return CorsFilter(source)
    }
}