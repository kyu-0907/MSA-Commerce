package com.example.batch.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "product")
class Product(
    @Id
    var id: String? = null,
    var name: String? = null,
    var price: Double? = null,
    var category: String? = null
) {
    // No-arg constructor is provided by kotlin-jpa plugin
}
