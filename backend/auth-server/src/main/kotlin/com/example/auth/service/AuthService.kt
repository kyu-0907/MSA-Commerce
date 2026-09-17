package com.example.auth.service

import com.example.auth.domain.User
import com.example.auth.dto.SignupRequest
import com.example.auth.dto.SignupResponse
import com.example.auth.exception.EmailAlreadyExistsException
import com.example.auth.exception.EmailNotVerifiedException
import com.example.auth.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailVerificationService: EmailVerificationService
) {

    fun requestEmailVerification(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw EmailAlreadyExistsException(email)
        }
        emailVerificationService.sendVerificationCode(email)
    }

    fun verifyEmail(email: String, code: String) {
        emailVerificationService.verifyCode(email, code)
    }

    @Transactional
    fun signup(request: SignupRequest): SignupResponse {
        if (!emailVerificationService.isVerified(request.email)) {
            throw EmailNotVerifiedException(request.email)
        }
        if (userRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException(request.email)
        }

        val user = userRepository.save(
            User(
                email = request.email,
                password = passwordEncoder.encode(request.password)
            )
        )

        emailVerificationService.clearVerified(request.email)

        return SignupResponse(
            id = user.id!!,
            email = user.email,
            createdAt = user.createdAt
        )
    }
}
