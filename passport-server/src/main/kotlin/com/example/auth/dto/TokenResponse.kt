package com.example.auth.dto

data class TokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long
)
