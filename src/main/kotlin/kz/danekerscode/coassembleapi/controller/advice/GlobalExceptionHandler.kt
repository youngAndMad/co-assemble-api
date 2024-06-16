package kz.danekerscode.coassembleapi.controller.advice

import kz.danekerscode.coassembleapi.model.exception.AuthProcessingException
import kz.danekerscode.coassembleapi.model.exception.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(AuthProcessingException::class)
    fun handleAuthProcessingException(ex: AuthProcessingException): ResponseEntity<Any> {
        val body = mapOf("error" to ex.message, "status" to ex.status)
        return ResponseEntity(body, ex.status)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<Any> {
        val body = mapOf("error" to ex.message, "status" to HttpStatus.NOT_FOUND)
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

}