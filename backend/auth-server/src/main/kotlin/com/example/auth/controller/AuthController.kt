package com.example.auth.controller

import com.example.auth.dto.EmailSignupRequest
import com.example.auth.dto.EmailSignupResponse
import com.example.auth.dto.EmailVerifyRequest
import com.example.auth.dto.EmailVerifyResponse
import com.example.auth.dto.SignupRequest
import com.example.auth.dto.SignupResponse
import com.example.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup/email")
    fun requestEmailVerification(
        @Valid @RequestBody request: EmailSignupRequest
    ): ResponseEntity<EmailSignupResponse> {
        authService.requestEmailVerification(request.email)
        return ResponseEntity.ok(EmailSignupResponse(request.email, "인증 코드를 이메일로 발송했습니다."))
    }

    @PostMapping("/signup/email/verify")
    fun verifyEmail(
        @Valid @RequestBody request: EmailVerifyRequest
    ): ResponseEntity<EmailVerifyResponse> {
        authService.verifyEmail(request.email, request.code)
        return ResponseEntity.ok(EmailVerifyResponse(request.email, true))
    }

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: SignupRequest): ResponseEntity<SignupResponse> {
        val response = authService.signup(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
