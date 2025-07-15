package com.example.quocard_cording_test.service

import com.example.quocard_cording_test.model.Author
import com.example.quocard_cording_test.repository.AuthorRepository
import com.example.quocard_cording_test.exception.ValidationException
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
    fun updateAuthorDateOfBirth(id: Long, dateOfBirth: LocalDate): Author {
        authorRepository.findById(id)
            ?: throw ValidationException("ID: ${id} の著者は見つかりません。")

        validateDateOfBirth(dateOfBirth)

        authorRepository.updateDateOfBirth(id, dateOfBirth)

        return authorRepository.findById(id)!!
    }

    private fun validateDateOfBirth(dateOfBirth: LocalDate) {
        if (!dateOfBirth.isBefore(LocalDate.now())) {
            throw ValidationException("生年月日は過去の日付である必要があります。")
        }
    }
}