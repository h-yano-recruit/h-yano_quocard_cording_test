package com.example.quocard_cording_test.dto

import com.example.quocard_cording_test.model.PublicationStatus
import java.math.BigDecimal

data class BookCreateRequest(
    val title: String,
    val price: BigDecimal,
    val status: PublicationStatus
)

data class BookResponse(
    val id: Long,
    val title: String,
    val price: BigDecimal,
    val status: PublicationStatus,
    val createdAt: String,
    val updatedAt: String
)