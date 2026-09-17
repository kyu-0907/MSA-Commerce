package com.example.auth.dto

data class EmailVerifyResponse(
    val email: String,
    val verified: Boolean
)
