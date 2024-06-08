package kz.danekerscode.coassembleapi.utils

import org.springframework.http.HttpCookie
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.util.SerializationUtils
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

object CookieUtils {

    fun getCookie(request: ServerHttpRequest, name: String): Mono<HttpCookie?> {
        return Mono
            .justOrEmpty(request.cookies[name]?.firstOrNull())
    }

    fun getCookie(request: ServerHttpResponse, name: String): Mono<HttpCookie?> {
        return Mono
            .justOrEmpty(request.cookies[name]?.firstOrNull())
    }

    fun addCookie(
        response: ServerHttpResponse,
        name: String,
        value: String,
        maxAge: Long
    ) {
        val cookie = ResponseCookie.from(name, value)
            .path("/")
            .httpOnly(true)
            .maxAge(maxAge)
            .build()

        response.addCookie(cookie)
    }

    fun deleteCookie(exchange: ServerWebExchange, name: String) {
        exchange.response.addCookie(
            ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .build()
        )
    }

    fun serialize(obj: Any): String {
        return Base64.getUrlEncoder()
            .encodeToString(SerializationUtils.serialize(obj))
    }

    fun <T> deserialize(cookie: HttpCookie, cls: Class<T>): T {
        return cls.cast(
            SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.value)
            )
        )
    }
}
