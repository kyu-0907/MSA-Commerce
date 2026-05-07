package com.example.order.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order,

    @Column(nullable = false)
    var productId: Long,

    @Column(nullable = false)
    var productName: String,

    @Column(nullable = false)
    var unitPrice: BigDecimal,

    @Column(nullable = false)
    var quantity: Int
)
