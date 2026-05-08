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
    fun signup(request: SignupRequest): Unit = TODO()

    fun login(request: LoginRequest): AuthResponse = TODO()
}
