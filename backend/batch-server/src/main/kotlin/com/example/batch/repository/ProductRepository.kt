package com.example.batch.repository

import com.example.batch.domain.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, String>
