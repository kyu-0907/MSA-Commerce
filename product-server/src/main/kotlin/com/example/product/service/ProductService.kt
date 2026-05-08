package com.example.product.service

import com.example.product.dto.ProductDto
import com.example.product.entity.Product
import com.example.product.repository.ProductRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun getAllProducts(): List<ProductDto> = TODO()

    fun getProduct(id: Long): ProductDto = TODO()

    fun getProductsByCategory(category: String): List<ProductDto> = TODO()

    fun searchProducts(keyword: String): List<ProductDto> = TODO()

    @Transactional
    fun createProduct(dto: ProductDto): ProductDto = TODO()

    @Transactional
    fun updateProduct(id: Long, dto: ProductDto): ProductDto = TODO()

    @Transactional
    fun deleteProduct(id: Long): Unit = TODO()

    @Transactional
    fun decreaseStock(id: Long, quantity: Int): Unit = TODO()

    private fun Product.toDto(): ProductDto = TODO()
}
