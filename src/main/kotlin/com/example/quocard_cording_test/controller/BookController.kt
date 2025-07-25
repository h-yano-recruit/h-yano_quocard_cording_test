package com.example.quocard_cording_test.controller

import com.example.quocard_cording_test.dto.BookCreateRequest
import com.example.quocard_cording_test.dto.BookResponse
import com.example.quocard_cording_test.dto.BookUpdateRequest
import com.example.quocard_cording_test.dto.toResponse
import com.example.quocard_cording_test.service.BookService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {

    @Operation(summary = "書籍の新規作成", description = "新しい書籍情報を登録します。")
    @PostMapping
    fun create(@RequestBody request: BookCreateRequest): ResponseEntity<BookResponse> {
        val createdBook = bookService.createBook(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdBook.toResponse())
    }

    @Operation(summary = "書籍の更新", description = "指定されたIDの書籍情報を更新します。")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: BookUpdateRequest
    ): ResponseEntity<BookResponse> {
        val updatedBook = bookService.updateBook(id, request)
        return ResponseEntity.ok(updatedBook.toResponse())
    }
}