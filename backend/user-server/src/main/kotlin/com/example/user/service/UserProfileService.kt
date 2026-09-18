package com.example.user.service

import com.example.user.domain.UserProfile
import com.example.user.dto.UserProfileCreateRequest
import com.example.user.dto.UserProfileResponse
import com.example.user.dto.UserProfileUpdateRequest
import com.example.user.exception.NicknameAlreadyExistsException
import com.example.user.exception.UserProfileAlreadyExistsException
import com.example.user.exception.UserProfileNotFoundException
import com.example.user.repository.UserProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProfileService(
    private val userProfileRepository: UserProfileRepository
) {

    @Transactional
    fun create(request: UserProfileCreateRequest): UserProfileResponse {
        if (userProfileRepository.existsByAuthUserId(request.authUserId)) {
            throw UserProfileAlreadyExistsException(request.authUserId)
        }
        if (userProfileRepository.existsByNickname(request.nickname)) {
            throw NicknameAlreadyExistsException(request.nickname)
        }

        val userProfile = userProfileRepository.save(
            UserProfile(
                authUserId = request.authUserId,
                nickname = request.nickname,
                phoneNumber = request.phoneNumber
            )
        )

        return UserProfileResponse.from(userProfile)
    }

    fun getByAuthUserId(authUserId: Long): UserProfileResponse {
        val userProfile = userProfileRepository.findByAuthUserId(authUserId)
            ?: throw UserProfileNotFoundException(authUserId)
        return UserProfileResponse.from(userProfile)
    }

    @Transactional
    fun update(authUserId: Long, request: UserProfileUpdateRequest): UserProfileResponse {
        val userProfile = userProfileRepository.findByAuthUserId(authUserId)
            ?: throw UserProfileNotFoundException(authUserId)

        if (request.nickname != null &&
            request.nickname != userProfile.nickname &&
            userProfileRepository.existsByNickname(request.nickname)
        ) {
            throw NicknameAlreadyExistsException(request.nickname)
        }

        userProfile.update(request.nickname, request.phoneNumber, request.profileImageUrl)

        return UserProfileResponse.from(userProfile)
    }
}
