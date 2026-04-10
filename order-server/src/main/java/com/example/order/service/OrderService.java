package com.example.order.service;

import com.example.order.dto.OrderDto;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public List<OrderDto> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + id));
        return toDto(order);
    }

    @Transactional
    public OrderDto createOrder(OrderDto dto) {
        Order order = Order.builder()
                .userId(dto.getUserId())
                .status(Order.OrderStatus.PENDING)
                .build();

        if (dto.getOrderItems() != null) {
            List<OrderItem> items = dto.getOrderItems().stream()
                    .map(itemDto -> OrderItem.builder()
                            .order(order)
                            .productId(itemDto.getProductId())
                            .productName(itemDto.getProductName())
                            .unitPrice(itemDto.getUnitPrice())
                            .quantity(itemDto.getQuantity())
                            .build())
                    .collect(Collectors.toList());
            order.getOrderItems().addAll(items);
        }

        Order saved = orderRepository.save(order);
        // 결제 서버로 주문 생성 이벤트 발행
        kafkaTemplate.send("order-created", "order-id:" + saved.getId() + ",user-id:" + saved.getUserId());
        return toDto(saved);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + id));
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        kafkaTemplate.send("order-status-changed", "order-id:" + id + ",status:" + status.name());
        return toDto(saved);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + id));
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        kafkaTemplate.send("order-cancelled", "order-id:" + id);
    }

    private OrderDto toDto(Order order) {
        List<OrderDto.OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(item -> OrderDto.OrderItemDto.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .orderItems(itemDtos)
                .build();
    }
}
