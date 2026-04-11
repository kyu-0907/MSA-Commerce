package com.example.push.service

import com.example.push.dto.PushRequest
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PushService {
    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun sendPush(request: PushRequest): Boolean {
        logger.info("푸시 전송 시도 중... [UserId: ${request.userId}, Title: ${request.title}]")
        
        // 외부 알림 서비스(FCM, APNs 등) 호출 시뮬레이션 (비동기 지연)
        delay(100) 
        
        logger.info("푸시 전송 완료: ${request.title}")
        return true
    }
}
