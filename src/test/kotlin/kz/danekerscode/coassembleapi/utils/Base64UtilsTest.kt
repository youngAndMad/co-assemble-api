package kz.danekerscode.coassembleapi.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class Base64UtilsTest {

    @Test
    fun `encodeToString should return base64 encoded string`() {
        val input = "Hello, World!"
        val expected = Base64.getEncoder().encodeToString(input.toByteArray())
        val actual = Base64Utils.encodeToString(input)
        assertEquals(expected, actual)
    }

    @Test
    fun `decodeToString should return base64 decoded string`() {
        val input = "SGVsbG8sIFdvcmxkIQ=="

        val expected = String(Base64.getDecoder().decode(input))

        val actual = Base64Utils.decodeToString(input)
        assertEquals(expected, actual)
    }

}