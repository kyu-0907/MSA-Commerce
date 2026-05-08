package com.example.order.service

import com.example.order.dto.OrderDto
import com.example.order.entity.Order
import com.example.order.entity.OrderItem
import com.example.order.repository.OrderRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderService(
    private val orderRepository: OrderRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun getOrdersByUser(userId: Long): List<OrderDto> = TODO()

    fun getOrder(id: Long): OrderDto = TODO()

    @Transactional
    fun createOrder(dto: OrderDto): OrderDto = TODO()

    @Transactional
    fun updateOrderStatus(id: Long, status: Order.OrderStatus): OrderDto = TODO()

    @Transactional
    fun cancelOrder(id: Long): Unit = TODO()

    private fun Order.toDto(): OrderDto = TODO()
}
