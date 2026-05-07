package com.example.auth.dto

data class AuthResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long
)
