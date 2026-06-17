package com.example.search.messaging

import com.example.search.domain.Product
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ProductEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val log = LoggerFactory.getLogger(ProductEventPublisher::class.java)

    companion object {
        private const val TOPIC = "product-events"
    }

    fun publishProductIndexed(product: Product) {
        log.info("[Kafka] Sending product-indexed event: id={}", product.id)
        kafkaTemplate.send(TOPIC, product.id!!, product)
    }
}
