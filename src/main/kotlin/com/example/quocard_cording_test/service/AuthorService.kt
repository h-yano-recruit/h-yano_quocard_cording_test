package com.example.quocard_cording_test.service

import com.example.quocard_cording_test.model.Author
import com.example.quocard_cording_test.repository.AuthorRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AuthorService(private val authorRepository: AuthorRepository) {

    fun createAuthor(name: String, dateOfBirth: LocalDate): Author {
        return authorRepository.create(name, dateOfBirth)
    }
}