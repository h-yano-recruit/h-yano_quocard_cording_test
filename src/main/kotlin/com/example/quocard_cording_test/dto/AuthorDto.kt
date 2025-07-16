package com.example.quocard_cording_test.dto

import java.time.LocalDate

data class AuthorCreateRequest(
    val name: String,
    val dateOfBirth: LocalDate
)

// 【新規】名前と生年月日を更新するためのリクエスト用DTO
data class AuthorUpdateRequest(
    val name: String,
    val dateOfBirth: LocalDate
)

data class AuthorResponse(
    val id: Long,
    val name: String,
    val dateOfBirth: LocalDate,
    val createdAt: String,
    val updatedAt: String
)