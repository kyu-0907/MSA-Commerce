package com.example.auth.service

import com.example.auth.dto.AuthResponse
import com.example.auth.dto.LoginRequest
import com.example.auth.dto.SignupRequest
import com.example.auth.entity.User
import com.example.auth.repository.UserRepository
import com.example.auth.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Transactional
    fun signup(request: SignupRequest) {
        if (userRepository.existsByUsername(request.username)) {
            throw RuntimeException("Username already exists")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw RuntimeException("Email already exists")
        }

        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            email = request.email,
            role = "ROLE_USER"
        )

        userRepository.save(user)
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByUsername(request.username)
            .orElseThrow { RuntimeException("User not found") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw RuntimeException("Invalid password")
        }

        val token = jwtTokenProvider.generateToken(user.username)

        return AuthResponse(
            accessToken = token,
            tokenType = "Bearer",
            expiresIn = jwtTokenProvider.expirationTime / 1000
        )
    }
}
