package com.example.push.service

import com.example.push.dto.PushRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PushService {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendPush(request: PushRequest) {
        logger.info("Push 발송: userId=${request.userId}, title=${request.title}")
    }
}
