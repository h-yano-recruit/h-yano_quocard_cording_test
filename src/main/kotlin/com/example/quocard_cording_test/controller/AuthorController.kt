package com.example.quocard_cording_test.controller

import com.example.quocard_cording_test.dto.AuthorCreateRequest
import com.example.quocard_cording_test.dto.AuthorDateOfBirthUpdateRequest
import com.example.quocard_cording_test.dto.AuthorResponse
import com.example.quocard_cording_test.dto.toResponse
import com.example.quocard_cording_test.service.AuthorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @PutMapping("/{id}")
    fun updateDateOfBirth(
        @PathVariable id: Long,
        @RequestBody request: AuthorDateOfBirthUpdateRequest
    ): ResponseEntity<AuthorResponse> {
        val updatedAuthor = authorService.updateAuthorDateOfBirth(id, request.dateOfBirth)
        return ResponseEntity.ok(updatedAuthor.toResponse())
    }
}