package com.example.user.dto

import com.example.user.domain.UserProfile
import java.time.LocalDateTime

data class UserProfileResponse(
    val id: Long,
    val authUserId: Long,
    val nickname: String,
    val phoneNumber: String?,
    val profileImageUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(userProfile: UserProfile): UserProfileResponse =
            UserProfileResponse(
                id = userProfile.id!!,
                authUserId = userProfile.authUserId,
                nickname = userProfile.nickname,
                phoneNumber = userProfile.phoneNumber,
                profileImageUrl = userProfile.profileImageUrl,
                createdAt = userProfile.createdAt,
                updatedAt = userProfile.updatedAt
            )
    }
}
