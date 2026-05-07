package com.example.auth.controller

import com.example.auth.dto.LoginRequest
import com.example.auth.dto.TokenResponse
import com.example.auth.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<*> {
        return try {
            val tokenResponse = authService.login(request)
            ResponseEntity.ok(tokenResponse)
        } catch (e: BadCredentialsException) {
            ResponseEntity.status(401).body(mapOf("error" to "Invalid credentials"))
        }
    }

    @GetMapping("/validate")
    fun validate(@RequestHeader("Authorization") authHeader: String?): ResponseEntity<*> {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body(mapOf("valid" to false, "error" to "Missing or invalid Authorization header"))
        }

        val token = authHeader.substring(7)
        val isValid = authService.validateToken(token)

        return if (isValid) {
            val username = authService.getUsernameFromToken(token)
            ResponseEntity.ok(mapOf("valid" to true, "username" to username))
        } else {
            ResponseEntity.status(401).body(mapOf("valid" to false, "error" to "Invalid or expired token"))
        }
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<*> {
        return ResponseEntity.ok(mapOf("status" to "UP", "service" to "passport-server"))
    }
}
