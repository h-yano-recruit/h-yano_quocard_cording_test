package com.example.quocard_cording_test.service

import com.example.quocard_cording_test.dto.BookCreateRequest
import com.example.quocard_cording_test.dto.BookUpdateRequest
import com.example.quocard_cording_test.exception.ValidationException
import com.example.quocard_cording_test.model.Book
import com.example.quocard_cording_test.model.PublicationStatus
import com.example.quocard_cording_test.repository.AuthorRepository
import com.example.quocard_cording_test.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
) {

    @Transactional
    fun createBook(request: BookCreateRequest): Book {
        validateBook(request.price, request.authorIds)

        return bookRepository.create(request.title, request.price, request.status, request.authorIds)
    }

    @Transactional
    fun updateBook(id: Long, request: BookUpdateRequest): Book {
        val currentBook = bookRepository.findById(id)
            ?: throw ValidationException("ID: $id の書籍は見つかりません。")

        if (currentBook.status == PublicationStatus.PUBLISHED && request.status == PublicationStatus.UNPUBLISHED) {
            throw ValidationException("出版済みの書籍を未出版に変更することはできません。")
        }

        validateBook(request.price, request.authorIds)

        bookRepository.update(id, request.title, request.price, request.status)

        bookRepository.updateBookAuthorAssociations(id, request.authorIds)

        return bookRepository.findById(id)!!
    }

    private fun validateBook(price: BigDecimal, authorIds: List<Long>) {
        if (price < BigDecimal.ZERO) {
            throw ValidationException("価格は0以上である必要があります。")
        }
        if (authorIds.isEmpty()) {
            throw ValidationException("書籍には最低1人の著者が必要です。")
        }

        val existingAuthorsCount = authorRepository.countExistingAuthors(authorIds)
        if (existingAuthorsCount != authorIds.size) {
            throw ValidationException("指定された著者IDの一部、またはすべてが存在しません。")
        }
    }
}