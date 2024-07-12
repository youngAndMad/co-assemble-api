package kz.danekerscode.coassembleapi.core.domain.errors

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidRequestPayloadException(override val message: String) : RuntimeException(message)
