package com.example.payment.service;

import com.example.payment.dto.PaymentDto;
import com.example.payment.entity.Payment;
import com.example.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PaymentDto getPaymentByOrder(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("결제를 찾을 수 없습니다. orderId: " + orderId));
        return toDto(payment);
    }

    public List<PaymentDto> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentDto processPayment(PaymentDto dto) {
        // 동일 주문 중복 결제 방지
        paymentRepository.findByOrderId(dto.getOrderId()).ifPresent(p -> {
            throw new RuntimeException("이미 결제가 진행된 주문입니다: " + dto.getOrderId());
        });

        // 외부 PG사 연동 시뮬레이션
        String transactionId = UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .orderId(dto.getOrderId())
                .userId(dto.getUserId())
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .status(Payment.PaymentStatus.COMPLETED)
                .transactionId(transactionId)
                .paidAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);
        // 주문 서버에 결제 완료 이벤트 발행
        kafkaTemplate.send("payment-completed",
                "order-id:" + saved.getOrderId() + ",transaction-id:" + transactionId);
        log.info("결제 완료 - orderId: {}, transactionId: {}", saved.getOrderId(), transactionId);
        return toDto(saved);
    }

    @Transactional
    public PaymentDto refundPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("결제를 찾을 수 없습니다: " + id));
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new RuntimeException("완료된 결제만 환불 가능합니다.");
        }
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        Payment saved = paymentRepository.save(payment);
        kafkaTemplate.send("payment-refunded",
                "order-id:" + saved.getOrderId() + ",payment-id:" + id);
        log.info("환불 처리 - paymentId: {}, orderId: {}", id, saved.getOrderId());
        return toDto(saved);
    }

    private PaymentDto toDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
