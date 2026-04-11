package com.example.push.controller

import com.example.push.dto.PushRequest
import com.example.push.service.PushService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/push")
class PushController(private val pushService: PushService) {

    @PostMapping("/send")
    suspend fun sendPush(@RequestBody request: PushRequest): Map<String, Any> {
        val success = pushService.sendPush(request)
        return mapOf(
            "success" to success,
            "message" to if (success) "푸시 발송 성공" else "푸시 발송 실패"
        )
    }
}
