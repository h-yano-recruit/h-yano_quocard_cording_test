package com.example.quocard_cording_test.service

import com.example.quocard_cording_test.dto.BookCreateRequest
import com.example.quocard_cording_test.model.Book
import com.example.quocard_cording_test.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(private val bookRepository: BookRepository) {

    @Transactional
    fun createBook(request: BookCreateRequest): Book {
        return bookRepository.create(request.title, request.price, request.status)
    }
}