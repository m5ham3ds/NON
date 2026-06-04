package com.example.domain.model

data class TestConfig(
    val routerId: Long = 0,
    val cardList: List<String> = emptyList(),
    val delayMs: Long = 500L
)
