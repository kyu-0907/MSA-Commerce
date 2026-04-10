package com.example.payment.kafka;

import com.example.payment.entity.Payment;
import com.example.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentRepository paymentRepository;

    /**
     * 주문 취소 이벤트를 수신하여 결제 상태를 REFUNDED로 변경
     */
    @KafkaListener(topics = "order-cancelled", groupId = "payment-group")
    public void handleOrderCancelled(String message) {
        log.info("주문 취소 이벤트 수신: {}", message);
        try {
            // message format: "order-id:{id}"
            String orderId = message.replace("order-id:", "").trim();
            paymentRepository.findByOrderId(Long.parseLong(orderId)).ifPresent(payment -> {
                if (payment.getStatus() == Payment.PaymentStatus.COMPLETED) {
                    payment.setStatus(Payment.PaymentStatus.REFUNDED);
                    paymentRepository.save(payment);
                    log.info("주문 취소로 인한 자동 환불 처리 완료 - orderId: {}", orderId);
                }
            });
        } catch (Exception e) {
            log.error("주문 취소 이벤트 처리 중 오류 발생: {}", e.getMessage());
        }
    }
}
