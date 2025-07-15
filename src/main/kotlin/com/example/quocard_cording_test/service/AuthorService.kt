package com.example.quocard_cording_test.service

import com.example.quocard_cording_test.model.Author
import com.example.quocard_cording_test.repository.AuthorRepository
import com.example.quocard_cording_test.exception.ValidationException
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AuthorService(private val authorRepository: AuthorRepository) {

    fun createAuthor(name: String, dateOfBirth: LocalDate): Author {
        if (!dateOfBirth.isBefore(LocalDate.now())) {
            throw ValidationException("生年月日は過去の日付である必要があります。")
        }

        return authorRepository.create(name, dateOfBirth)
    }
}