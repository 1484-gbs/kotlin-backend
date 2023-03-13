package com.example.demo.exception.handler

import com.example.demo.exception.ApplicationException
import com.example.demo.exception.InvalidRequestException
import com.example.demo.exception.NotFoundException
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.core.convert.ConversionFailedException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class ErrorResponse(
        val message: String?,
        val detail: String? = null,
        val cause: String? = null,
        val details: List<String>? = null,
    )

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFoundException(e: Exception): ErrorResponse {
        return ErrorResponse(
            message = e.message,
        )
    }

    @ExceptionHandler(
        InvalidRequestException::class,
        ConversionFailedException::class
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun badRequest(e: Exception): ErrorResponse {
        return ErrorResponse(
            message = "invalid request.",
            detail = e.message
        )
    }


    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {

        return ResponseEntity(
            ErrorResponse(
                message = "invalid request.",
                details = ex.bindingResult.fieldErrors.asSequence().map { e ->
                    "${e.field}: ${e.defaultMessage}"
                }.toList(),
            ),
            headers,
            status
        )
    }

    @ExceptionHandler(ApplicationException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun applicationException(e: Exception): ErrorResponse {
        return ErrorResponse(
            message = e.message
        )
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun internalServerError(e: Exception): ErrorResponse {
        return ErrorResponse(
            message = "system error.",
            detail = e.message,
            cause = e.cause?.toString()
        )
    }
}