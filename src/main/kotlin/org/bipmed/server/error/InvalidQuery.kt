package org.bipmed.server.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(reason = "Invalid query.", value = HttpStatus.BAD_REQUEST)
class InvalidQuery : RuntimeException()
