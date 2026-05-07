package com.example.payment.controller

import com.example.payment.dto.PaymentDto
import com.example.payment.service.PaymentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService
) {

    @GetMapping("/order/{orderId}")
    fun getPaymentByOrder(@PathVariable orderId: Long): ResponseEntity<PaymentDto> {
        return ResponseEntity.ok(paymentService.getPaymentByOrder(orderId))
    }

    @GetMapping("/user/{userId}")
    fun getPaymentsByUser(@PathVariable userId: Long): ResponseEntity<List<PaymentDto>> {
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId))
    }

    @PostMapping
    fun processPayment(@RequestBody dto: PaymentDto): ResponseEntity<PaymentDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(dto))
    }

    @PostMapping("/{id}/refund")
    fun refundPayment(@PathVariable id: Long): ResponseEntity<PaymentDto> {
        return ResponseEntity.ok(paymentService.refundPayment(id))
    }
}
