package com.example.payment.service

import com.example.payment.dto.PaymentDto
import com.example.payment.entity.Payment
import com.example.payment.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional(readOnly = true)
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    private val log = LoggerFactory.getLogger(PaymentService::class.java)

    fun getPaymentByOrder(orderId: Long): PaymentDto = TODO()

    fun getPaymentsByUser(userId: Long): List<PaymentDto> = TODO()

    @Transactional
    fun processPayment(dto: PaymentDto): PaymentDto = TODO()

    @Transactional
    fun refundPayment(id: Long): PaymentDto = TODO()

    private fun Payment.toDto(): PaymentDto = TODO()
}
