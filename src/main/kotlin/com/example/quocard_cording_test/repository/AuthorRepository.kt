package com.example.quocard_cording_test.repository

import com.example.quocard_cording_test.jooq.generated.tables.Authors
import com.example.quocard_cording_test.jooq.generated.tables.records.AuthorsRecord
import com.example.quocard_cording_test.jooq.generated.tables.references.AUTHORS
import com.example.quocard_cording_test.jooq.generated.tables.references.BOOKS
import com.example.quocard_cording_test.jooq.generated.tables.references.BOOK_AUTHORS
import com.example.quocard_cording_test.model.Author
import com.example.quocard_cording_test.model.Book
import com.example.quocard_cording_test.model.PublicationStatus
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

    fun findBooksByAuthorId(authorId: Long): List<Book> {
        val ba1 = BOOK_AUTHORS.`as`("ba1")
        val ba2 = BOOK_AUTHORS.`as`("ba2")

        val result = dslContext.select(
            BOOKS.asterisk(),
            AUTHORS.asterisk()
        )
            .from(ba1)
            .join(ba2).on(ba1.BOOK_ID.eq(ba2.BOOK_ID))
            .join(BOOKS).on(ba2.BOOK_ID.eq(BOOKS.ID))
            .join(AUTHORS).on(ba2.AUTHOR_ID.eq(AUTHORS.ID))
            .where(ba1.AUTHOR_ID.eq(authorId))
            .fetch()

        return result.intoGroups(BOOKS.ID)
            .map { (_, records) ->
                val firstRecord = records.first()
                val authors = records.map { record ->
                    Author(
                        id = record.get(AUTHORS.ID)!!,
                        name = record.get(AUTHORS.NAME)!!,
                        dateOfBirth = record.get(AUTHORS.DATE_OF_BIRTH)!!,
                        createdAt = record.get(AUTHORS.CREATED_AT)!!,
                        updatedAt = record.get(AUTHORS.UPDATED_AT)!!
                    )
                }.distinct()

                Book(
                    id = firstRecord.get(BOOKS.ID)!!,
                    title = firstRecord.get(BOOKS.TITLE)!!,
                    price = firstRecord.get(BOOKS.PRICE)!!,
                    status = PublicationStatus.fromCode(firstRecord.get(BOOKS.STATUS)!!),
                    authors = authors,
                    createdAt = firstRecord.get(BOOKS.CREATED_AT)!!,
                    updatedAt = firstRecord.get(BOOKS.UPDATED_AT)!!
                )
            }
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