package com.example.order.dto

import com.example.order.entity.Order
import java.math.BigDecimal

data class OrderDto(
    val id: Long? = null,
    val userId: Long,
    val status: Order.OrderStatus? = null,
    val orderItems: List<OrderItemDto>? = null
) {
    data class OrderItemDto(
        val productId: Long,
        val productName: String,
        val unitPrice: BigDecimal,
        val quantity: Int
    )
}
