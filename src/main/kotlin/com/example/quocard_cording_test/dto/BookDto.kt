package com.example.quocard_cording_test.dto

import com.example.quocard_cording_test.model.PublicationStatus
import java.math.BigDecimal

data class BookCreateRequest(
    val title: String,
    val price: BigDecimal,
    val status: PublicationStatus,
    val authorIds: List<Long> // 最低1人の著者が必須
)

data class BookUpdateRequest(
    val title: String,
    val price: BigDecimal,
    val status: PublicationStatus,
    val authorIds: List<Long>
)

data class BookResponse(
    val id: Long,
    val title: String,
    val price: BigDecimal,
    val status: PublicationStatus,
    val authors: List<AuthorResponse>,
    val createdAt: String,
    val updatedAt: String
)