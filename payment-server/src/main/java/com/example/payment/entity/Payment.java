package com.example.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String transactionId;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = PaymentStatus.PENDING;
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED
    }

    public enum PaymentMethod {
        CARD, BANK_TRANSFER, VIRTUAL_ACCOUNT
    }
}
