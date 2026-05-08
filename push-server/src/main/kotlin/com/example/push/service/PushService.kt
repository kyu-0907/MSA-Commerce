package com.example.push.service

import com.example.push.dto.PushRequest
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PushService {
    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun sendPush(request: PushRequest): Boolean = TODO()
}
