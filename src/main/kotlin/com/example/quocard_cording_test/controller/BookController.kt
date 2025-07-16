package com.example.quocard_cording_test.controller

import com.example.quocard_cording_test.dto.BookCreateRequest
import com.example.quocard_cording_test.dto.BookResponse
import com.example.quocard_cording_test.dto.BookUpdateRequest
import com.example.quocard_cording_test.dto.toResponse
import com.example.quocard_cording_test.service.BookService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    fun create(@RequestBody request: BookCreateRequest): ResponseEntity<BookResponse> {
        val createdBook = bookService.createBook(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdBook.toResponse())
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: BookUpdateRequest
    ): ResponseEntity<BookResponse> {
        val updatedBook = bookService.updateBook(id, request)
        return ResponseEntity.ok(updatedBook.toResponse())
    }
}