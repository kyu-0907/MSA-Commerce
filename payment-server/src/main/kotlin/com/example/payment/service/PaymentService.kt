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

    fun getPaymentByOrder(orderId: Long): PaymentDto {
        val payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow { RuntimeException("결제를 찾을 수 없습니다. orderId: $orderId") }
        return payment.toDto()
    }

    fun getPaymentsByUser(userId: Long): List<PaymentDto> {
        return paymentRepository.findByUserId(userId).map { it.toDto() }
    }

    @Transactional
    fun processPayment(dto: PaymentDto): PaymentDto {
        paymentRepository.findByOrderId(dto.orderId).ifPresent {
            throw RuntimeException("이미 결제가 진행된 주문입니다: ${dto.orderId}")
        }

        val transactionId = UUID.randomUUID().toString()

        val payment = Payment(
            orderId = dto.orderId,
            userId = dto.userId,
            amount = dto.amount,
            paymentMethod = dto.paymentMethod,
            status = Payment.PaymentStatus.COMPLETED,
            transactionId = transactionId,
            paidAt = LocalDateTime.now()
        )

        val saved = paymentRepository.save(payment)
        kafkaTemplate.send("payment-completed", "order-id:${saved.orderId},transaction-id:$transactionId")
        log.info("결제 완료 - orderId: {}, transactionId: {}", saved.orderId, transactionId)
        return saved.toDto()
    }

    @Transactional
    fun refundPayment(id: Long): PaymentDto {
        val payment = paymentRepository.findById(id)
            .orElseThrow { RuntimeException("결제를 찾을 수 없습니다: $id") }
        if (payment.status != Payment.PaymentStatus.COMPLETED) {
            throw RuntimeException("완료된 결제만 환불 가능합니다.")
        }
        payment.status = Payment.PaymentStatus.REFUNDED
        val saved = paymentRepository.save(payment)
        kafkaTemplate.send("payment-refunded", "order-id:${saved.orderId},payment-id:$id")
        log.info("환불 처리 - paymentId: {}, orderId: {}", id, saved.orderId)
        return saved.toDto()
    }

    private fun Payment.toDto(): PaymentDto {
        return PaymentDto(
            id = id,
            orderId = orderId,
            userId = userId,
            amount = amount,
            status = status,
            paymentMethod = paymentMethod,
            transactionId = transactionId,
            createdAt = createdAt,
            paidAt = paidAt
        )
    }
}
