package com.example.user.repository

import com.example.user.domain.UserProfile
import org.springframework.data.jpa.repository.JpaRepository

interface UserProfileRepository : JpaRepository<UserProfile, Long> {
    fun existsByAuthUserId(authUserId: Long): Boolean
    fun existsByNickname(nickname: String): Boolean
    fun findByAuthUserId(authUserId: Long): UserProfile?
}
