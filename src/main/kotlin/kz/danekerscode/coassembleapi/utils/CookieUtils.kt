package kz.danekerscode.coassembleapi.utils

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.util.SerializationUtils
import java.util.*

object CookieUtils {

    fun getCookie(request: HttpServletRequest?, name: String?): Cookie? =
        request?.cookies?.firstOrNull { cookie: Cookie -> cookie.name == name }

    fun addCookie(response: HttpServletResponse, name: String?, value: String?, maxAge: Int): Unit =
        Cookie(name, value).apply {
            path = "/"
            isHttpOnly = true
            this.maxAge = maxAge
        }.let {
            response.addCookie(it)
        }

    fun deleteCookie(request: HttpServletRequest, response: HttpServletResponse, name: String) =
        request.cookies
            .filter { cookie: Cookie -> cookie.name == name }
            .map { cookie: Cookie ->
                cookie.value = ""
                cookie.path = "/"
                cookie.maxAge = 0
                cookie
            }
            .forEach {
                response.addCookie(it)
            }

    fun serialize(`object`: Any?): String = Base64.getUrlEncoder()
        .encodeToString(SerializationUtils.serialize(`object`))

    fun <T> deserialize(cookie: Cookie, cls: Class<T>): T = cls.cast(
        SerializationUtils.deserialize(
            Base64.getUrlDecoder().decode(cookie.value)
        )
    )
}
