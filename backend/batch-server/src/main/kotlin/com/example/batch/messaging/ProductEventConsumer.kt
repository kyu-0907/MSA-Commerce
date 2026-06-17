package com.example.batch.messaging

import com.example.batch.domain.Product
import com.example.batch.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ProductEventConsumer(
    private val productRepository: ProductRepository
) {
    private val log = LoggerFactory.getLogger(ProductEventConsumer::class.java)

    @KafkaListener(topics = ["product-events"], groupId = "batch-server-group")
    fun consumeProductEvent(product: Product) {
        log.info("[Kafka] Received product-indexed event: id={}, name={}", product.id, product.name)
        try {
            productRepository.save(product)
            log.info("[Kafka] Product saved to MySQL: id={}", product.id)
        } catch (e: Exception) {
            log.error("[Kafka] Failed to save product: id={}, error={}", product.id, e.message)
        }
    }
}
