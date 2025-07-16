package com.example.quocard_cording_test.dto

import com.example.quocard_cording_test.model.Author
import com.example.quocard_cording_test.model.Book
import java.time.format.DateTimeFormatter

fun Author.toResponse(): AuthorResponse {
    return AuthorResponse(
        id = this.id,
        name = this.name,
        dateOfBirth = this.dateOfBirth,
        createdAt = this.createdAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        updatedAt = this.updatedAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    )
}

fun Book.toResponse(): BookResponse {
    return BookResponse(
        id = this.id,
        title = this.title,
        price = this.price,
        status = this.status,
        authors = this.authors.map { it.toResponse() },
        createdAt = this.createdAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        updatedAt = this.updatedAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    )
}
