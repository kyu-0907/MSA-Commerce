package com.example.order.dto;

import com.example.order.entity.Order;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private Long userId;
    private Order.OrderStatus status;
    private List<OrderItemDto> orderItems;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private BigDecimal unitPrice;
        private Integer quantity;
    }
}
