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

    fun getAllProducts(): List<ProductDto> {
        return productRepository.findAll().map { it.toDto() }
    }

    fun getProduct(id: Long): ProductDto {
        val product = productRepository.findById(id)
            .orElseThrow { RuntimeException("상품을 찾을 수 없습니다: $id") }
        return product.toDto()
    }

    fun getProductsByCategory(category: String): List<ProductDto> {
        return productRepository.findByCategory(category).map { it.toDto() }
    }

    fun searchProducts(keyword: String): List<ProductDto> {
        return productRepository.findByNameContainingIgnoreCase(keyword).map { it.toDto() }
    }

    @Transactional
    fun createProduct(dto: ProductDto): ProductDto {
        val product = Product(
            name = dto.name,
            description = dto.description,
            price = dto.price,
            stock = dto.stock,
            category = dto.category
        )
        val saved = productRepository.save(product)
        kafkaTemplate.send("product-created", "product-id:${saved.id}")
        return saved.toDto()
    }

    @Transactional
    fun updateProduct(id: Long, dto: ProductDto): ProductDto {
        val product = productRepository.findById(id)
            .orElseThrow { RuntimeException("상품을 찾을 수 없습니다: $id") }
        product.name = dto.name
        product.description = dto.description
        product.price = dto.price
        product.stock = dto.stock
        product.category = dto.category
        return productRepository.save(product).toDto()
    }

    @Transactional
    fun deleteProduct(id: Long) {
        productRepository.deleteById(id)
        kafkaTemplate.send("product-deleted", "product-id:$id")
    }

    @Transactional
    fun decreaseStock(id: Long, quantity: Int) {
        val product = productRepository.findById(id)
            .orElseThrow { RuntimeException("상품을 찾을 수 없습니다: $id") }
        if (product.stock < quantity) {
            throw RuntimeException("재고가 부족합니다. 현재 재고: ${product.stock}")
        }
        product.stock -= quantity
        productRepository.save(product)
    }

    private fun Product.toDto(): ProductDto {
        return ProductDto(
            id = id,
            name = name,
            description = description,
            price = price,
            stock = stock,
            category = category
        )
    }
}
