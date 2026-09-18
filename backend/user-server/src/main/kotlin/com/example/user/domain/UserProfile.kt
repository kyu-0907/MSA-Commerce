package com.example.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "user_profiles")
class UserProfile(
    @Column(name = "auth_user_id", nullable = false, unique = true)
    val authUserId: Long,

    @Column(nullable = false, unique = true)
    var nickname: String,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "profile_image_url")
    var profileImageUrl: String? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set

    fun update(nickname: String?, phoneNumber: String?, profileImageUrl: String?) {
        nickname?.let { this.nickname = it }
        phoneNumber?.let { this.phoneNumber = it }
        profileImageUrl?.let { this.profileImageUrl = it }
        this.updatedAt = LocalDateTime.now()
    }
}
