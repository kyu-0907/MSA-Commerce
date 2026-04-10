package com.example.payment.controller;

import com.example.payment.dto.PaymentDto;
import com.example.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDto> getPaymentByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrder(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
    }

    @PostMapping
    public ResponseEntity<PaymentDto> processPayment(@RequestBody PaymentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(dto));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentDto> refundPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }
}
