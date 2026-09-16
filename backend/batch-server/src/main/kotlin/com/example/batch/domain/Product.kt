package com.example.batch.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Product(
    @Id
    val id: String? = null,
    val name: String = "",
    val price: Double = 0.0,
    val category: String = ""
)
