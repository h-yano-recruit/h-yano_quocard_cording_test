package com.example.quocard_cording_test.service

import com.example.quocard_cording_test.dto.AuthorUpdateRequest
import com.example.quocard_cording_test.exception.ValidationException
import com.example.quocard_cording_test.model.Author
import com.example.quocard_cording_test.model.Book
import com.example.quocard_cording_test.repository.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class AuthorService(private val authorRepository: AuthorRepository) {

    fun createAuthor(name: String, dateOfBirth: LocalDate): Author {
        validateDateOfBirth(dateOfBirth)
        return authorRepository.create(name, dateOfBirth)
    }

    @Transactional
    fun updateAuthor(id: Long, request: AuthorUpdateRequest): Author {
        authorRepository.findById(id)
            ?: throw ValidationException("ID: ${id} の著者は見つかりません。")

        val dateOfBirth = request.dateOfBirth
        validateDateOfBirth(dateOfBirth)

        authorRepository.update(id, request.name, dateOfBirth)

        return authorRepository.findById(id)!!
    }

    fun findBooksByAuthor(id: Long): List<Book> {
        authorRepository.findById(id)
            ?: throw ValidationException("ID: ${id} の著者は見つかりません。")

        return authorRepository.findBooksByAuthorId(id)
    }

    private fun validateDateOfBirth(dateOfBirth: LocalDate) {
        if (!dateOfBirth.isBefore(LocalDate.now())) {
            throw ValidationException("生年月日は過去の日付である必要があります。")
        }
    }
}