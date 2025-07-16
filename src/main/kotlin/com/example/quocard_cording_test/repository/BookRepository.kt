package com.example.quocard_cording_test.repository

import com.example.quocard_cording_test.jooq.generated.tables.records.BooksRecord
import com.example.quocard_cording_test.jooq.generated.tables.references.BOOKS
import com.example.quocard_cording_test.jooq.generated.tables.references.AUTHORS
import com.example.quocard_cording_test.jooq.generated.tables.references.BOOK_AUTHORS
import com.example.quocard_cording_test.model.Book
import com.example.quocard_cording_test.model.Author
import com.example.quocard_cording_test.model.PublicationStatus
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class BookRepository(private val dslContext: DSLContext) {

    fun create(title: String, price: BigDecimal, status: PublicationStatus, authorIds: List<Long>): Book {
        val bookRecord = dslContext.insertInto(BOOKS)
            .set(BOOKS.TITLE, title)
            .set(BOOKS.PRICE, price)
            .set(BOOKS.STATUS, status.code)
            .returning()
            .fetchOne()!!

        val newBookId = bookRecord.id!!

        if (authorIds.isNotEmpty()) {
            val rows = authorIds.map { authorId ->
                dslContext.newRecord(BOOK_AUTHORS).apply {
                    this.bookId = newBookId
                    this.authorId = authorId
                }
            }
            dslContext.batchInsert(rows).execute()
        }

        return findById(newBookId) ?: throw IllegalStateException("作成した書籍が見つかりません: ID $newBookId")
    }

    fun findById(id: Long): Book? {
        val bookRecord = dslContext.selectFrom(BOOKS)
            .where(BOOKS.ID.eq(id))
            .fetchOne() ?: return null

        val authors = dslContext.select(AUTHORS.asterisk())
            .from(AUTHORS)
            .join(BOOK_AUTHORS).on(AUTHORS.ID.eq(BOOK_AUTHORS.AUTHOR_ID))
            .where(BOOK_AUTHORS.BOOK_ID.eq(id))
            .fetchInto(Author::class.java)

        return recordToBook(bookRecord, authors)
    }

    private fun recordToBook(record: BooksRecord, authors: List<Author>): Book {
        return Book(
            id = record.id!!,
            title = record.title!!,
            price = record.price!!,
            status = PublicationStatus.fromCode(record.status!!),
            authors = authors,
            createdAt = record.createdAt!!,
            updatedAt = record.updatedAt!!
        )
    }
}