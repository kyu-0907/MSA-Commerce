package com.example.auth.service;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    // 실습용 인메모리 사용자 저장소
    private static final Map<String, String> USERS = new HashMap<>();

    static {
        USERS.put("user", "password");
        USERS.put("admin", "admin123");
    }

    public TokenResponse login(LoginRequest request) {
        String storedPassword = USERS.get(request.getUsername());
        if (storedPassword == null || !storedPassword.equals(request.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = generateToken(request.getUsername());
        return new TokenResponse(token, "Bearer", expirationMs / 1000);
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    private String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    private Claims getClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
