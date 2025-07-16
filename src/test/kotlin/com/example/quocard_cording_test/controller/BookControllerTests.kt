package com.example.quocard_cording_test.controller

import com.example.quocard_cording_test.dto.BookCreateRequest
import com.example.quocard_cording_test.dto.BookUpdateRequest
import com.example.quocard_cording_test.exception.ValidationException
import com.example.quocard_cording_test.model.Book
import com.example.quocard_cording_test.model.PublicationStatus
import com.example.quocard_cording_test.service.BookService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.OffsetDateTime

@WebMvcTest(BookController::class)
class BookControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var bookService: BookService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `create - 異常系 - リクエストが無効な場合に400エラーが返ること`() {
        val requestBody = mapOf(
            "title" to "不正な書籍",
            "price" to -100,
            "status" to 0,
            "authorIds" to listOf(1L)
        )
        `when`(bookService.createBook(any())).thenThrow(ValidationException("価格は0以上である必要があります。"))

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("価格は0以上である必要があります。"))
    }

    @Test
    fun `update - 異常系 - 存在しない書籍を更新しようとすると400エラーが返ること`() {
        val bookId = 999L
        val requestBody = mapOf(
            "title" to "更新後書籍", "price" to 1500, "status" to 1, "authorIds" to listOf(1L)
        )
        `when`(bookService.updateBook(eq(bookId), any()))
            .thenThrow(ValidationException("ID: $bookId の書籍は見つかりません。"))

        mockMvc.perform(
            put("/api/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("ID: 999 の書籍は見つかりません。"))
    }


    @Test
    fun `create - 正常系 - 新しい書籍を正常に作成できること`() {
        val requestBody = mapOf(
            "title" to "テスト書籍",
            "price" to 1500,
            "status" to 0,
            "authorIds" to listOf(1L)
        )
        val createdBook = Book(
            id = 1L, title = "テスト書籍", price = BigDecimal("1500"), status = PublicationStatus.UNPUBLISHED,
            authors = emptyList(), createdAt = OffsetDateTime.now(), updatedAt = OffsetDateTime.now()
        )
        `when`(bookService.createBook(any())).thenReturn(createdBook)

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("テスト書籍"))
    }


    @Test
    fun `update - 正常系 - 既存の書籍を正常に更新できること`() {
        val bookId = 1L
        val requestBody = mapOf(
            "title" to "更新後書籍", "price" to 1800, "status" to 1, "authorIds" to listOf(1L)
        )
        val updatedBook = Book(
            id = bookId, title = "更新後書籍", price = BigDecimal("1800"), status = PublicationStatus.PUBLISHED,
            authors = emptyList(), createdAt = OffsetDateTime.now(), updatedAt = OffsetDateTime.now()
        )
        `when`(bookService.updateBook(eq(bookId), any())).thenReturn(updatedBook)

        mockMvc.perform(
            put("/api/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(bookId))
            .andExpect(jsonPath("$.title").value("更新後書籍"))
    }
}