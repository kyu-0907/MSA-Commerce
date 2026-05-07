package com.example.product.dto

import java.math.BigDecimal

data class ProductDto(
    val id: Long? = null,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stock: Int,
    val category: String
)
