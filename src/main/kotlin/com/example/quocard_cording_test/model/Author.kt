package com.example.quocard_cording_test.model

import java.time.LocalDate
import java.time.OffsetDateTime

data class Author(
    val id: Long,
    val name: String,
    val dateOfBirth: LocalDate,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)