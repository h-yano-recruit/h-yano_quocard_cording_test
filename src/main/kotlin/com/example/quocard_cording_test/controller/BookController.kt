package com.example.quocard_cording_test.controller

import com.example.quocard_cording_test.dto.BookCreateRequest
import com.example.quocard_cording_test.dto.BookResponse
import com.example.quocard_cording_test.dto.toResponse
import com.example.quocard_cording_test.model.Book
import com.example.quocard_cording_test.service.BookService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeFormatter

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

    private fun Book.toResponse(): BookResponse {
        return BookResponse(
            id = this.id,
            title = this.title,
            price = this.price,
            status = this.status,
            authors = this.authors.map { it.toResponse() },
            createdAt = this.createdAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            updatedAt = this.updatedAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
    }
}