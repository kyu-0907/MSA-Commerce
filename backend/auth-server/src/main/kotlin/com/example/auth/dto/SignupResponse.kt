package com.example.auth.dto

import java.time.LocalDateTime

data class SignupResponse(
    val id: Long,
    val email: String,
    val createdAt: LocalDateTime
)
