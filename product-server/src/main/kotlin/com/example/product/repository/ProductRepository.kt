package com.example.product.repository

import com.example.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {
    fun findByCategory(category: String): List<Product>
    fun findByNameContainingIgnoreCase(keyword: String): List<Product>
}
