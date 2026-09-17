package com.example.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailSignupRequest(
    @field:NotBlank
    @field:Email
    val email: String
)
