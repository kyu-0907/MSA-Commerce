package com.example.user.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UserProfileCreateRequest(
    @field:NotNull
    val authUserId: Long,

    @field:NotBlank
    val nickname: String,

    val phoneNumber: String? = null
)

data class UserProfileUpdateRequest(
    val nickname: String? = null,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null
)
