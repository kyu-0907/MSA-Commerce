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
    fun login(@RequestBody request: LoginRequest): ResponseEntity<*> = TODO()

    @GetMapping("/validate")
    fun validate(@RequestHeader("Authorization") authHeader: String?): ResponseEntity<*> = TODO()

    @GetMapping("/health")
    fun health(): ResponseEntity<*> = TODO()
}
