package com.example.payment.kafka

import com.example.payment.entity.Payment
import com.example.payment.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentEventConsumer(
    private val paymentRepository: PaymentRepository
) {
    private val log = LoggerFactory.getLogger(PaymentEventConsumer::class.java)

    @KafkaListener(topics = ["order-cancelled"], groupId = "payment-group")
    fun handleOrderCancelled(message: String) {
        log.info("주문 취소 이벤트 수신: {}", message)
        try {
            val orderId = message.replace("order-id:", "").trim()
            paymentRepository.findByOrderId(orderId.toLong()).ifPresent { payment ->
                if (payment.status == Payment.PaymentStatus.COMPLETED) {
                    payment.status = Payment.PaymentStatus.REFUNDED
                    paymentRepository.save(payment)
                    log.info("주문 취소로 인한 자동 환불 처리 완료 - orderId: {}", orderId)
                }
            }
        } catch (e: Exception) {
            log.error("주문 취소 이벤트 처리 중 오류 발생: {}", e.message)
        }
    }
}
