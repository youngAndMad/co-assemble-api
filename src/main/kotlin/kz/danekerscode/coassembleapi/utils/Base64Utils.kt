package kz.danekerscode.coassembleapi.utils

import java.util.*

/**
 * Utils for base64 encoding and decoding
 * */
object Base64Utils {

    /**
     * Encode string to base64
     * */
    fun encodeToString(input: String): String {
        val bytes = input.toByteArray()
        return Base64.getEncoder().encodeToString(bytes)
    }

    /**
     * Decode base64 to string
     * */
    fun decodeToString(input: String): String {
        val bytes = Base64.getDecoder().decode(input)
        return String(bytes)
    }

}