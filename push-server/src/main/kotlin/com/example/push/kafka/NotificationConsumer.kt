package com.example.push.kafka

import com.example.push.dto.PushRequest
import com.example.push.service.PushService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NotificationConsumer(private val pushService: PushService) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val scope = CoroutineScope(Dispatchers.Default)

    @KafkaListener(topics = ["order-created", "payment-completed"], groupId = "push-group")
    fun handleNotification(message: String) {
        logger.info("Kafka 메시지 수신: $message")
        
        // Coroutine을 통한 비동기 처리
        scope.launch {
            try {
                // 메시지 파싱 시뮬레이션
                val pushRequest = PushRequest(
                    userId = extractUserId(message),
                    title = "알림",
                    body = "새로운 이벤트가 발생했습니다: $message",
                    type = "TOPIC_EVENT"
                )
                pushService.sendPush(pushRequest)
            } catch (e: Exception) {
                logger.error("푸시 처리 중 오류 발생: ${e.message}")
            }
        }
    }

    private fun extractUserId(message: String): Long {
        // message format: "order-id:1,user-id:123"
        return try {
            message.split(",")
                .find { it.contains("user-id") }
                ?.split(":")
                ?.get(1)
                ?.toLong() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}
