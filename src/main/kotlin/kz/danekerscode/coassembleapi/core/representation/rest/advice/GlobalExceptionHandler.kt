package kz.danekerscode.coassembleapi.core.representation.rest.advice

import kz.danekerscode.coassembleapi.core.domain.errors.AuthProcessingException
import kz.danekerscode.coassembleapi.core.domain.errors.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(AuthProcessingException::class)
    fun handleAuthProcessingException(ex: AuthProcessingException): ResponseEntity<ProblemDetail> =
        ResponseEntity(
            ProblemDetail.forStatusAndDetail(
                ex.status,
                ex.message,
            ),
            ex.status,
        )

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<ProblemDetail> =
        ResponseEntity(
            ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.message,
            ),
            HttpStatus.NOT_FOUND,
        )
}
