package com.example.quocard_cording_test

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@OpenAPIDefinition(
	info = Info(
		title = "書籍・著者管理API",
		version = "1.0.0",
		description = "クオカード社コーディングテスト: 書籍・著書管理のためのAPI仕様書です。"
	)
)

@SpringBootApplication
class QuocardCordingTestApplication

fun main(args: Array<String>) {
	runApplication<QuocardCordingTestApplication>(*args)
}
