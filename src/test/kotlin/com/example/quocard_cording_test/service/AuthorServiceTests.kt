package com.example.quocard_cording_test.service

import com.example.quocard_cording_test.dto.AuthorUpdateRequest
import com.example.quocard_cording_test.exception.ValidationException
import com.example.quocard_cording_test.model.Author
import com.example.quocard_cording_test.model.Book
import com.example.quocard_cording_test.model.PublicationStatus
import com.example.quocard_cording_test.repository.AuthorRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

class AuthorServiceTests {

    @InjectMocks
    private lateinit var authorService: AuthorService

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `createAuthor - 異常系 - 生年月日が未来の日付の場合にValidationExceptionが発生すること`() {
        val name = "未来の著者"
        val futureDate = LocalDate.now().plusDays(1) // 未来の日付

        val exception = assertThrows<ValidationException> {
            authorService.createAuthor(name, futureDate)
        }
        assertEquals("生年月日は過去の日付である必要があります。", exception.message)
    }

    @Test
    fun `updateAuthor - 異常系 - 存在しない著者を更新しようとするとValidationExceptionが発生すること`() {
        val authorId = 999L
        val request = AuthorUpdateRequest("更新後の名前", LocalDate.of(1995, 5, 5))

        `when`(authorRepository.findById(authorId)).thenReturn(null)

        val exception = assertThrows<ValidationException> {
            authorService.updateAuthor(authorId, request)
        }
        assertEquals("ID: 999 の著者は見つかりません。", exception.message)
    }

    @Test
    fun `findBooksByAuthor - 異常系 - 存在しない著者の書籍を取得しようとするとValidationExceptionが発生すること`() {
        val authorId = 999L // 存在しないID

        `when`(authorRepository.findById(authorId)).thenReturn(null)

        val exception = assertThrows<ValidationException> {
            authorService.findBooksByAuthor(authorId)
        }
        assertEquals("ID: 999 の著者は見つかりません。", exception.message)
    }

    @Test
    fun `createAuthor - 正常系 - 著者を正常に作成できること`() {
        val name = "新しい著者"
        val dateOfBirth = LocalDate.of(1990, 1, 1)
        val author = Author(1L, name, dateOfBirth, OffsetDateTime.now(), OffsetDateTime.now())

        `when`(authorRepository.create(name, dateOfBirth)).thenReturn(author)

        val result = authorService.createAuthor(name, dateOfBirth)

        assertEquals(name, result.name)
        verify(authorRepository).create(name, dateOfBirth)
    }

    @Test
    fun `updateAuthor - 正常系 - 著者を正常に更新できること`() {
        val authorId = 1L
        val request = AuthorUpdateRequest("更新後の名前", LocalDate.of(1995, 5, 5))
        val existingAuthor =
            Author(authorId, "更新前の名前", LocalDate.of(1990, 1, 1), OffsetDateTime.now(), OffsetDateTime.now())
        val updatedAuthor =
            Author(authorId, request.name, request.dateOfBirth, OffsetDateTime.now(), OffsetDateTime.now())

        `when`(authorRepository.findById(authorId))
            .thenReturn(existingAuthor)
            .thenReturn(updatedAuthor)

        val result = authorService.updateAuthor(authorId, request)

        assertEquals("更新後の名前", result.name)
        verify(authorRepository).update(authorId, request.name, request.dateOfBirth)
    }

    @Test
    fun `findBooksByAuthor - 正常系 - 著者に紐づく書籍リストを正常に取得できること`() {
        val authorId = 1L
        val author = Author(authorId, "テスト著者", LocalDate.now(), OffsetDateTime.now(), OffsetDateTime.now())
        val books = listOf(
            Book(
                101L,
                "テスト書籍",
                BigDecimal.ONE,
                PublicationStatus.PUBLISHED,
                listOf(author),
                OffsetDateTime.now(),
                OffsetDateTime.now()
            )
        )

        `when`(authorRepository.findById(authorId)).thenReturn(author)
        `when`(authorRepository.findBooksByAuthorId(authorId)).thenReturn(books)

        val result = authorService.findBooksByAuthor(authorId)

        assertEquals(1, result.size)
        assertEquals("テスト書籍", result[0].title)
        verify(authorRepository).findBooksByAuthorId(authorId)
    }
}