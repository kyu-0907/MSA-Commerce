package com.example.auth.service

import com.example.auth.dto.LoginRequest
import com.example.auth.dto.TokenResponse
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class AuthService {

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    @Value("\${jwt.expiration}")
    private var expirationMs: Long = 0

    companion object {
        private val USERS = mapOf(
            "user" to "password",
            "admin" to "admin123"
        )
    }

    fun login(request: LoginRequest): TokenResponse = TODO()

    fun validateToken(token: String): Boolean = TODO()

    fun getUsernameFromToken(token: String): String = TODO()

    private fun generateToken(username: String): String = TODO()

    private fun getClaims(token: String): Claims = TODO()
}
