package com.example.demo.exception.handler

import com.example.demo.exception.ApplicationException
import com.example.demo.exception.InvalidRequestException
import com.example.demo.exception.NotFoundException
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.core.convert.ConversionFailedException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class ErrorResponse(
        val message: String?,
        val detail: String? = null,
        val cause: String? = null
    )

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFoundException(e: Exception): ErrorResponse {
        return ErrorResponse(
            message = e.message,
        )
    }

    @ExceptionHandler(
        HttpMessageNotReadableException::class,
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
            cause = e.cause.toString()
        )
    }
}