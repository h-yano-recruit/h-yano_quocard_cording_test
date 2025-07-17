package com.example.quocard_cording_test.controller

import com.example.quocard_cording_test.dto.AuthorUpdateRequest
import com.example.quocard_cording_test.exception.ValidationException
import com.example.quocard_cording_test.model.Author
import com.example.quocard_cording_test.model.Book
import com.example.quocard_cording_test.model.PublicationStatus
import com.example.quocard_cording_test.service.AuthorService
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

@WebMvcTest(AuthorController::class)
class AuthorControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var authorService: AuthorService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `create - 異常系 - リクエストが無効な場合に400エラーが返ること`() {
        val requestBody = mapOf("name" to "未来の著者", "dateOfBirth" to "2999-01-01")
        `when`(
            authorService.createAuthor(
                any(),
                any()
            )
        ).thenThrow(ValidationException("生年月日は過去の日付である必要があります。"))

        mockMvc.perform(
            post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("生年月日は過去の日付である必要があります。"))
    }

    @Test
    fun `update - 異常系 - 存在しない著者を更新しようとすると400エラーが返ること`() {
        val authorId = 999L
        val requestBody = mapOf("name" to "更新後テスト著者", "dateOfBirth" to "2002-02-02")

        `when`(authorService.updateAuthor(eq(authorId), any()))
            .thenThrow(ValidationException("ID: $authorId の著者は見つかりません。"))

        mockMvc.perform(
            put("/api/authors/{id}", authorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("ID: 999 の著者は見つかりません。"))
    }

    @Test
    fun `findBooksByAuthor - 異常系 - 存在しない著者を指定した場合に400エラーが返ること`() {
        val authorId = 999L

        `when`(authorService.findBooksByAuthor(authorId)).thenThrow(ValidationException("ID: $authorId の著者は見つかりません。"))

        mockMvc.perform(get("/api/authors/{id}/books", authorId))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("ID: 999 の著者は見つかりません。"))
    }

    @Test
    fun `create - 正常系 - 新しい著者を正常に作成できること`() {
        val requestBody = mapOf("name" to "新規テスト著者", "dateOfBirth" to "2001-01-01")
        val createdAuthor =
            Author(1L, "新規テスト著者", LocalDate.of(2001, 1, 1), OffsetDateTime.now(), OffsetDateTime.now())

        `when`(authorService.createAuthor("新規テスト著者", LocalDate.of(2001, 1, 1))).thenReturn(createdAuthor)

        mockMvc.perform(
            post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("新規テスト著者"))
    }

    @Test
    fun `update - 正常系 - 既存の著者を正常に更新できること`() {
        val authorId = 1L
        val requestBody = mapOf("name" to "更新後テスト著者", "dateOfBirth" to "2002-02-02")
        AuthorUpdateRequest("更新後テスト著者", LocalDate.of(2002, 2, 2))
        val updatedAuthor =
            Author(authorId, "更新後テスト著者", LocalDate.of(2002, 2, 2), OffsetDateTime.now(), OffsetDateTime.now())

        `when`(authorService.updateAuthor(eq(authorId), any())).thenReturn(updatedAuthor)


        // --- 実行 & 検証 ---
        mockMvc.perform(
            put("/api/authors/{id}", authorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(authorId))
            .andExpect(jsonPath("$.name").value("更新後テスト著者"))
    }

    @Test
    fun `findBooksByAuthor - 正常系 - 著者の書籍リストを正しく取得できること`() {
        val authorId = 1L
        val author = Author(authorId, "テスト著者", LocalDate.now(), OffsetDateTime.now(), OffsetDateTime.now())
        val books = listOf(
            Book(
                101L,
                "テスト書籍1",
                BigDecimal.TEN,
                PublicationStatus.PUBLISHED,
                listOf(author),
                OffsetDateTime.now(),
                OffsetDateTime.now()
            )
        )

        `when`(authorService.findBooksByAuthor(authorId)).thenReturn(books)

        mockMvc.perform(get("/api/authors/{id}/books", authorId))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$[0].id").value(101L))
            .andExpect(jsonPath("$[0].title").value("テスト書籍1"))
            .andExpect(jsonPath("$[0].authors[0].name").value("テスト著者"))
    }

}