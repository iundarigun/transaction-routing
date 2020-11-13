package br.com.devcave.jpa.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.NOT_FOUND)
class EntityNotFoundException(message: String) : RuntimeException(message)