package com.example.quocard_cording_test.controller

import com.example.quocard_cording_test.dto.AuthorCreateRequest
import com.example.quocard_cording_test.dto.AuthorResponse
import com.example.quocard_cording_test.model.Author
import com.example.quocard_cording_test.service.AuthorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/authors")
class AuthorController(private val authorService: AuthorService) {

    @PostMapping
    fun create(@RequestBody request: AuthorCreateRequest): ResponseEntity<AuthorResponse> {
        val createdAuthor = authorService.createAuthor(request.name, request.dateOfBirth)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdAuthor.toResponse())
    }

    private fun Author.toResponse(): AuthorResponse {
        return AuthorResponse(
            id = this.id,
            name = this.name,
            dateOfBirth = this.dateOfBirth,
            createdAt = this.createdAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            updatedAt = this.updatedAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
    }
}