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
    fun getPaymentByOrder(@PathVariable orderId: Long): ResponseEntity<PaymentDto> = TODO()

    @GetMapping("/user/{userId}")
    fun getPaymentsByUser(@PathVariable userId: Long): ResponseEntity<List<PaymentDto>> = TODO()

    @PostMapping
    fun processPayment(@RequestBody dto: PaymentDto): ResponseEntity<PaymentDto> = TODO()

    @PostMapping("/{id}/refund")
    fun refundPayment(@PathVariable id: Long): ResponseEntity<PaymentDto> = TODO()
}
