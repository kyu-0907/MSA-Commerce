package com.example.user.controller

import com.example.user.dto.UserProfileCreateRequest
import com.example.user.dto.UserProfileResponse
import com.example.user.dto.UserProfileUpdateRequest
import com.example.user.service.UserProfileService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserProfileController(
    private val userProfileService: UserProfileService
) {

    @PostMapping
    fun create(@Valid @RequestBody request: UserProfileCreateRequest): ResponseEntity<UserProfileResponse> {
        val response = userProfileService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{authUserId}")
    fun get(@PathVariable authUserId: Long): ResponseEntity<UserProfileResponse> {
        return ResponseEntity.ok(userProfileService.getByAuthUserId(authUserId))
    }

    @PatchMapping("/{authUserId}")
    fun update(
        @PathVariable authUserId: Long,
        @RequestBody request: UserProfileUpdateRequest
    ): ResponseEntity<UserProfileResponse> {
        return ResponseEntity.ok(userProfileService.update(authUserId, request))
    }
}
