package com.example.auth.dto

data class SignupRequest(
    val username: String = "",
    val password: String = "",
    val email: String = ""
)
