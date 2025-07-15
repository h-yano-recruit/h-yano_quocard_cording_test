package com.example.quocard_cording_test.repository

import com.example.quocard_cording_test.jooq.generated.tables.Authors
import com.example.quocard_cording_test.jooq.generated.tables.records.AuthorsRecord
import com.example.quocard_cording_test.jooq.generated.tables.references.AUTHORS
import com.example.quocard_cording_test.model.Author
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class AuthorRepository(private val dslContext: DSLContext) {

    fun create(name: String, dateOfBirth: LocalDate): Author {
        val record = dslContext.insertInto(Authors.AUTHORS)
            .set(AUTHORS.NAME, name)
            .set(AUTHORS.DATE_OF_BIRTH, dateOfBirth)
            .returning()
            .fetchOne()!!

        return recordToAuthor(record)
    }

    private fun recordToAuthor(record: AuthorsRecord): Author {
        return Author(
            id = record.id!!,
            name = record.name!!,
            dateOfBirth = record.dateOfBirth!!,
            createdAt = record.createdAt!!,
            updatedAt = record.updatedAt!!
        )
    }
}