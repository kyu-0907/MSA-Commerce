package com.example.auth.controller;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.TokenResponse;
import com.example.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /auth/login
     * Body: { "username": "user", "password": "password" }
     * Response: { "accessToken": "...", "tokenType": "Bearer", "expiresIn": 3600 }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            TokenResponse tokenResponse = authService.login(request);
            return ResponseEntity.ok(tokenResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    /**
     * GET /auth/validate
     * Header: Authorization: Bearer <token>
     * Response: { "valid": true/false, "username": "..." }
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body(Map.of("valid", false, "error", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        boolean isValid = authService.validateToken(token);

        if (isValid) {
            String username = authService.getUsernameFromToken(token);
            return ResponseEntity.ok(Map.of("valid", true, "username", username));
        } else {
            return ResponseEntity.status(401).body(Map.of("valid", false, "error", "Invalid or expired token"));
        }
    }

    /**
     * GET /auth/health
     * 헬스체크 엔드포인트
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "passport-server"));
    }
}
