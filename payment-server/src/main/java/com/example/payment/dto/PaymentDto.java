package com.example.payment.dto;

import com.example.payment.entity.Payment;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private Payment.PaymentStatus status;
    private Payment.PaymentMethod paymentMethod;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
