package com.example.quocard_cording_test.repository

import com.example.quocard_cording_test.jooq.generated.tables.records.BooksRecord
import com.example.quocard_cording_test.jooq.generated.tables.references.BOOKS
import com.example.quocard_cording_test.model.Book
import com.example.quocard_cording_test.model.PublicationStatus
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class BookRepository(private val dslContext: DSLContext) {

    fun create(title: String, price: BigDecimal, status: PublicationStatus): Book {
        val record = dslContext.insertInto(BOOKS)
            .set(BOOKS.TITLE, title)
            .set(BOOKS.PRICE, price)
            .set(BOOKS.STATUS, status.code)
            .returning()
            .fetchOne()!!

        return recordToBook(record)
    }

    private fun recordToBook(record: BooksRecord): Book {
        return Book(
            id = record.id!!,
            title = record.title!!,
            price = record.price!!,
            status = PublicationStatus.fromCode(record.status!!),
            createdAt = record.createdAt!!,
            updatedAt = record.updatedAt!!
        )
    }
}