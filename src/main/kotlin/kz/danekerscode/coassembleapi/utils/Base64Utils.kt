package kz.danekerscode.coassembleapi.utils

import java.util.*

/**
 * Utils for base64 encoding and decoding
 * */
object Base64Utils {
    fun encodeToString(input: String): String = encodeToString(input.toByteArray())

    private fun encodeToString(input: ByteArray?): String = Base64.getEncoder().encodeToString(input)

    fun decodeToString(input: String): String = String(Base64.getDecoder().decode(input))
}
