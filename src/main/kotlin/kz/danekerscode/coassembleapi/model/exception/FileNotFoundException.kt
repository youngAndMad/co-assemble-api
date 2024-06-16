package kz.danekerscode.coassembleapi.model.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler

class FileNotFoundException(id: String) : RuntimeException("File not found with id: $id") {

    @ExceptionHandler(AuthProcessingException::class)
    fun handleAuthProcessingException(ex: AuthProcessingException): ResponseEntity<Any> {
        val body = mapOf("error" to ex.message, "status" to ex.status)
        return ResponseEntity(body, ex.status)
    }

    @ExceptionHandler(FileNotFoundException::class)
    fun handleFileNotFoundException(ex: FileNotFoundException): ResponseEntity<Any> {
        val body = mapOf("error" to ex.message, "status" to HttpStatus.NOT_FOUND)
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

}