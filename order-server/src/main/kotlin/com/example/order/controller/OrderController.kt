package com.example.order.controller

import com.example.order.dto.OrderDto
import com.example.order.entity.Order
import com.example.order.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {

    @GetMapping("/user/{userId}")
    fun getOrdersByUser(@PathVariable userId: Long): ResponseEntity<List<OrderDto>> {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId))
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: Long): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.getOrder(id))
    }

    @PostMapping
    fun createOrder(@RequestBody dto: OrderDto): ResponseEntity<OrderDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(dto))
    }

    @PatchMapping("/{id}/status")
    fun updateOrderStatus(
        @PathVariable id: Long,
        @RequestParam status: Order.OrderStatus
    ): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status))
    }

    @DeleteMapping("/{id}")
    fun cancelOrder(@PathVariable id: Long): ResponseEntity<Void> {
        orderService.cancelOrder(id)
        return ResponseEntity.noContent().build()
    }
}
