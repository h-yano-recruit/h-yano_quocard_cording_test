package com.example.quocard_cording_test.controller

import com.example.quocard_cording_test.dto.*
import com.example.quocard_cording_test.service.AuthorService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/authors")
class AuthorController(private val authorService: AuthorService) {

    @Operation(summary = "著者の新規作成", description = "新しい著者情報を登録します。")
    @PostMapping
    fun create(@RequestBody request: AuthorCreateRequest): ResponseEntity<AuthorResponse> {
        val createdAuthor = authorService.createAuthor(request.name, request.dateOfBirth)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdAuthor.toResponse())
    }

    @Operation(summary = "著者の更新", description = "指定されたIDの著者情報を更新します。")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: AuthorUpdateRequest
    ): ResponseEntity<AuthorResponse> {
        val updatedAuthor = authorService.updateAuthor(id, request)
        return ResponseEntity.ok(updatedAuthor.toResponse())
    }

    @Operation(summary = "著者に紐づく書籍の取得", description = "指定されたIDの著者が執筆した書籍の一覧を取得します。")
    @GetMapping("/{id}/books")
    fun findBooksByAuthor(@PathVariable id: Long): ResponseEntity<List<BookResponse>> {
        val books = authorService.findBooksByAuthor(id)
        val response = books.map { it.toResponse() }
        return ResponseEntity.ok(response)
    }
}