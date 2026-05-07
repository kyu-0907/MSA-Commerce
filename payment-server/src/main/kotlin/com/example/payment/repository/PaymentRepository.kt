package com.example.payment.repository

import com.example.payment.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PaymentRepository : JpaRepository<Payment, Long> {
    fun findByOrderId(orderId: Long): Optional<Payment>
    fun findByUserId(userId: Long): List<Payment>
    fun findByStatus(status: Payment.PaymentStatus): List<Payment>
}
