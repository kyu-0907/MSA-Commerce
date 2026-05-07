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

    fun login(request: LoginRequest): TokenResponse {
        val storedPassword = USERS[request.username]
        if (storedPassword == null || storedPassword != request.password) {
            throw BadCredentialsException("Invalid username or password")
        }

        val token = generateToken(request.username)
        return TokenResponse(token, "Bearer", expirationMs / 1000)
    }

    fun validateToken(token: String): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUsernameFromToken(token: String): String {
        return getClaims(token).subject
    }

    private fun generateToken(username: String): String {
        val key: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray())
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(key)
            .compact()
    }

    private fun getClaims(token: String): Claims {
        val key: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray())
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
