package com.example.payment.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var orderId: Long,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false)
    var amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PaymentStatus = PaymentStatus.PENDING,

    @Enumerated(EnumType.STRING)
    var paymentMethod: PaymentMethod? = null,

    var transactionId: String? = null,
    var paidAt: LocalDateTime? = null
) {
    @Column(updatable = false)
    var createdAt: LocalDateTime? = null

    @PrePersist
    fun prePersist() {
        createdAt = LocalDateTime.now()
    }

    enum class PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED
    }

    enum class PaymentMethod {
        CARD, BANK_TRANSFER, VIRTUAL_ACCOUNT
    }
}
