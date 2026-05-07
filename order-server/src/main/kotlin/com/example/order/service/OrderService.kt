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

    fun getOrdersByUser(userId: Long): List<OrderDto> {
        return orderRepository.findByUserId(userId).map { it.toDto() }
    }

    fun getOrder(id: Long): OrderDto {
        val order = orderRepository.findById(id)
            .orElseThrow { RuntimeException("주문을 찾을 수 없습니다: $id") }
        return order.toDto()
    }

    @Transactional
    fun createOrder(dto: OrderDto): OrderDto {
        val order = Order(
            userId = dto.userId,
            status = Order.OrderStatus.PENDING
        )

        dto.orderItems?.let { items ->
            val orderItems = items.map { itemDto ->
                OrderItem(
                    order = order,
                    productId = itemDto.productId,
                    productName = itemDto.productName,
                    unitPrice = itemDto.unitPrice,
                    quantity = itemDto.quantity
                )
            }
            order.orderItems.addAll(orderItems)
        }

        val saved = orderRepository.save(order)
        // 결제 서버로 주문 생성 이벤트 발행
        kafkaTemplate.send("order-created", "order-id:${saved.id},user-id:${saved.userId}")
        return saved.toDto()
    }

    @Transactional
    fun updateOrderStatus(id: Long, status: Order.OrderStatus): OrderDto {
        val order = orderRepository.findById(id)
            .orElseThrow { RuntimeException("주문을 찾을 수 없습니다: $id") }
        order.status = status
        val saved = orderRepository.save(order)
        kafkaTemplate.send("order-status-changed", "order-id:$id,status:${status.name}")
        return saved.toDto()
    }

    @Transactional
    fun cancelOrder(id: Long) {
        val order = orderRepository.findById(id)
            .orElseThrow { RuntimeException("주문을 찾을 수 없습니다: $id") }
        order.status = Order.OrderStatus.CANCELLED
        orderRepository.save(order)
        kafkaTemplate.send("order-cancelled", "order-id:$id")
    }

    private fun Order.toDto(): OrderDto {
        val itemDtos = orderItems.map { item ->
            OrderDto.OrderItemDto(
                productId = item.productId,
                productName = item.productName,
                unitPrice = item.unitPrice,
                quantity = item.quantity
            )
        }

        return OrderDto(
            id = id,
            userId = userId,
            status = status,
            orderItems = itemDtos
        )
    }
}
