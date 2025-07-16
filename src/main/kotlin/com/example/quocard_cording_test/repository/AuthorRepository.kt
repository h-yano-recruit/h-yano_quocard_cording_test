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

    fun update(id: Long, name: String, dateOfBirth: LocalDate): Int {
        return dslContext.update(AUTHORS)
            .set(AUTHORS.NAME, name) // ★nameの更新を追加
            .set(AUTHORS.DATE_OF_BIRTH, dateOfBirth)
            .where(AUTHORS.ID.eq(id))
            .execute()
    }
    fun findById(id: Long): Author? {
        return dslContext.selectFrom(AUTHORS)
            .where(AUTHORS.ID.eq(id))
            .fetchOne(this::recordToAuthor)
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

    fun countExistingAuthors(authorIds: List<Long>): Int {
        if (authorIds.isEmpty()) {
            return 0
        }
        return dslContext.selectCount()
            .from(AUTHORS)
            .where(AUTHORS.ID.`in`(authorIds))
            .fetchOne(0, Int::class.java) ?: 0
    }
}