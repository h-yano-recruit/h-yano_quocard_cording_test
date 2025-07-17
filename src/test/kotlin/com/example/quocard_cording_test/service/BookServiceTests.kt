package com.example.quocard_cording_test.service

import com.example.quocard_cording_test.dto.BookCreateRequest
import com.example.quocard_cording_test.dto.BookUpdateRequest
import com.example.quocard_cording_test.exception.ValidationException
import com.example.quocard_cording_test.model.Author
import com.example.quocard_cording_test.model.Book
import com.example.quocard_cording_test.model.PublicationStatus
import com.example.quocard_cording_test.repository.AuthorRepository
import com.example.quocard_cording_test.repository.BookRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

class BookServiceTests {

    @InjectMocks
    private lateinit var bookService: BookService

    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `createBook - 価格がマイナスの場合にValidationExceptionが発生すること`() {
        val request = BookCreateRequest(
            title = "不正な書籍",
            price = BigDecimal("-100"),
            status = PublicationStatus.UNPUBLISHED,
            authorIds = listOf(101L)
        )

        val exception = assertThrows<ValidationException> {
            bookService.createBook(request)
        }
        assertEquals("価格は0以上である必要があります。", exception.message)
    }

    @Test
    fun `createBook - 著者IDが空の場合にValidationExceptionが発生すること`() {
        val request = BookCreateRequest(
            title = "著者なし書籍",
            price = BigDecimal("1500"),
            status = PublicationStatus.UNPUBLISHED,
            authorIds = emptyList()
        )

        val exception = assertThrows<ValidationException> {
            bookService.createBook(request)
        }
        assertEquals("書籍には最低1人の著者が必要です。", exception.message)

        verify(bookRepository, never()).create(any(), any(), any(), any())
    }

    @Test
    fun `createBook - 存在しない著者IDが含まれる場合にValidationExceptionが発生すること`() {
        val request = BookCreateRequest(
            title = "不正な書籍",
            price = BigDecimal("1000"),
            status = PublicationStatus.UNPUBLISHED,
            authorIds = listOf(101L, 999L)
        )

        `when`(authorRepository.countExistingAuthors(request.authorIds)).thenReturn(1)

        val exception = assertThrows<ValidationException> {
            bookService.createBook(request)
        }
        assertEquals("指定された著者IDの一部、またはすべてが存在しません。", exception.message)
    }

    @Test
    fun `updateBook - 価格がマイナスの場合にValidationExceptionが発生すること`() {
        // --- 準備 ---
        val bookId = 1L
        val updateRequest = BookUpdateRequest(
            title = "新しいタイトル",
            price = BigDecimal("-1"),
            status = PublicationStatus.PUBLISHED,
            authorIds = listOf(101L)
        )

        `when`(bookRepository.findById(bookId)).thenReturn(
            Book(
                bookId,
                "",
                BigDecimal.ZERO,
                PublicationStatus.UNPUBLISHED,
                emptyList(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
            )
        )

        val exception = assertThrows<ValidationException> {
            bookService.updateBook(bookId, updateRequest)
        }
        assertEquals("価格は0以上である必要があります。", exception.message)
    }

    @Test
    fun `updateBook - 著者IDが空の場合にValidationExceptionが発生すること`() {
        val bookId = 1L
        val updateRequest = BookUpdateRequest(
            title = "新しいタイトル",
            price = BigDecimal("1000"),
            status = PublicationStatus.PUBLISHED,
            authorIds = emptyList()
        )

        `when`(bookRepository.findById(bookId)).thenReturn(
            Book(
                bookId,
                "",
                BigDecimal.ZERO,
                PublicationStatus.UNPUBLISHED,
                emptyList(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
            )
        )

        val exception = assertThrows<ValidationException> {
            bookService.updateBook(bookId, updateRequest)
        }
        assertEquals("書籍には最低1人の著者が必要です。", exception.message)
    }

    @Test
    fun `updateBook - 存在しない著者IDが含まれる場合にValidationExceptionが発生すること`() {
        val bookId = 1L
        val updateRequest = BookUpdateRequest(
            title = "新しいタイトル",
            price = BigDecimal("1000"),
            status = PublicationStatus.PUBLISHED,
            authorIds = listOf(101L, 999L)
        )

        `when`(bookRepository.findById(bookId)).thenReturn(
            Book(
                bookId,
                "",
                BigDecimal.ZERO,
                PublicationStatus.UNPUBLISHED,
                emptyList(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
            )
        )
        `when`(authorRepository.countExistingAuthors(updateRequest.authorIds)).thenReturn(1)

        val exception = assertThrows<ValidationException> {
            bookService.updateBook(bookId, updateRequest)
        }
        assertEquals("指定された著者IDの一部、またはすべてが存在しません。", exception.message)
    }

    @Test
    fun `updateBook - 出版済みの書籍を未出版に変更しようとするとValidationExceptionが発生すること`() {
        val bookId = 1L
        val authorId = 101L

        val currentBook = Book(
            id = bookId,
            title = "既存のタイトル",
            price = BigDecimal("1000"),
            status = PublicationStatus.PUBLISHED,
            authors = listOf(Author(authorId, "著者", LocalDate.now(), OffsetDateTime.now(), OffsetDateTime.now())),
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )

        val updateRequest = BookUpdateRequest(
            title = "新しいタイトル",
            price = BigDecimal("1200"),
            status = PublicationStatus.UNPUBLISHED,
            authorIds = listOf(authorId)
        )

        `when`(bookRepository.findById(bookId)).thenReturn(currentBook)

        val exception = assertThrows<ValidationException> {
            bookService.updateBook(bookId, updateRequest)
        }

        assertEquals("出版済みの書籍を未出版に変更することはできません。", exception.message)

        verify(bookRepository, never()).update(any(), any(), any(), any())
    }

    @Test
    fun `createBook - 書籍を正常に作成できること`() {
        val request = BookCreateRequest(
            title = "新しい書籍",
            price = BigDecimal("2000"),
            status = PublicationStatus.UNPUBLISHED,
            authorIds = listOf(101L)
        )
        val createdBook = Book(
            id = 1L, title = request.title, price = request.price, status = request.status,
            authors = emptyList(), // ここはダミーでOK
            createdAt = OffsetDateTime.now(), updatedAt = OffsetDateTime.now()
        )

        `when`(authorRepository.countExistingAuthors(request.authorIds)).thenReturn(1)
        `when`(bookRepository.create(request.title, request.price, request.status, request.authorIds)).thenReturn(
            createdBook
        )

        val result = bookService.createBook(request)

        assertEquals("新しい書籍", result.title)
        verify(bookRepository).create(request.title, request.price, request.status, request.authorIds)
    }

    @Test
    fun `updateBook - 正常な更新が成功すること`() {
        val bookId = 1L
        val authorId = 101L
        val currentBook = Book( // 現在は「未出版」
            id = bookId, title = "古いタイトル", price = BigDecimal("1000"), status = PublicationStatus.UNPUBLISHED,
            authors = listOf(Author(authorId, "著者", LocalDate.now(), OffsetDateTime.now(), OffsetDateTime.now())),
            createdAt = OffsetDateTime.now(), updatedAt = OffsetDateTime.now()
        )
        val updateRequest = BookUpdateRequest(
            title = "新しいタイトル", price = BigDecimal("1200"), status = PublicationStatus.PUBLISHED,
            authorIds = listOf(authorId)
        )

        val updatedBook =
            currentBook.copy(title = "新しいタイトル", price = BigDecimal("1200"), status = PublicationStatus.PUBLISHED)

        `when`(bookRepository.findById(bookId)).thenReturn(currentBook).thenReturn(updatedBook)
        `when`(authorRepository.countExistingAuthors(listOf(authorId))).thenReturn(1)

        val result = bookService.updateBook(bookId, updateRequest)

        assertEquals("新しいタイトル", result.title)
        assertEquals(PublicationStatus.PUBLISHED, result.status)

        verify(bookRepository).update(bookId, updateRequest.title, updateRequest.price, updateRequest.status)
        verify(bookRepository).updateBookAuthorAssociations(bookId, updateRequest.authorIds)
    }
}