package com.example.payment.dto

import com.example.payment.entity.Payment
import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentDto(
    val id: Long? = null,
    val orderId: Long,
    val userId: Long,
    val amount: BigDecimal,
    val status: Payment.PaymentStatus? = null,
    val paymentMethod: Payment.PaymentMethod? = null,
    val transactionId: String? = null,
    val createdAt: LocalDateTime? = null,
    val paidAt: LocalDateTime? = null
)
