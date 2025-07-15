package com.example.quocard_cording_test.advice

import com.example.quocard_cording_test.exception.ValidationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(e: ValidationException): ResponseEntity<out Map<String, Any>> {
        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to HttpStatus.BAD_REQUEST.reasonPhrase,
            "message" to (e.message ?: "無効なリクエストです。")
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

}