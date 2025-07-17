package com.example.quocard_cording_test.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.OffsetDateTime

@Schema(
    description = "出版状況 (0: 未出版, 1: 出版済み)",
)
enum class PublicationStatus(@JsonValue val code: Int) {
    UNPUBLISHED(0),
    PUBLISHED(1);

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromCode(code: Int): PublicationStatus {
            return entries.firstOrNull { it.code == code }
                ?: throw IllegalArgumentException("不明な出版状況です: $code")
        }
    }
}
data class Book(
    val id: Long,
    val title: String,
    val price: BigDecimal,
    val status: PublicationStatus,
    val authors: List<Author>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)