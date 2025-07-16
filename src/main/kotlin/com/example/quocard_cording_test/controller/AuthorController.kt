package com.example.quocard_cording_test.controller

import com.example.quocard_cording_test.dto.AuthorCreateRequest
import com.example.quocard_cording_test.dto.AuthorUpdateRequest
import com.example.quocard_cording_test.dto.AuthorResponse
import com.example.quocard_cording_test.dto.BookResponse
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
    fun update(
        @PathVariable id: Long,
        @RequestBody request: AuthorUpdateRequest
    ): ResponseEntity<AuthorResponse> {
        val updatedAuthor = authorService.updateAuthor(id, request)
        return ResponseEntity.ok(updatedAuthor.toResponse())
    }

    @GetMapping("/{id}/books")
    fun findBooksByAuthor(@PathVariable id: Long): ResponseEntity<List<BookResponse>> {
        val books = authorService.findBooksByAuthor(id)
        val response = books.map { it.toResponse() }
        return ResponseEntity.ok(response)
    }
}