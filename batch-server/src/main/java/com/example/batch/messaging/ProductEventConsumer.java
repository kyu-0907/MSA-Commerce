package com.example.batch.messaging;

import com.example.batch.domain.Product;
import com.example.batch.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final ProductRepository productRepository;

    /**
     * search-server가 ES에 상품을 색인할 때 발행하는 이벤트를 소비해 MySQL에 저장
     * 원본 ElasticBatchConfig의 역할:
     *   - reader: elasticProductRepository.findAll() (ES 조회)
     *   - writer: jpaProductRepository.saveAll()    (MySQL 저장)
     * →  MSA에서는 Kafka를 통해 두 서비스를 비동기적으로 연결
     */
    @KafkaListener(topics = "product-events", groupId = "batch-server-group")
    public void consumeProductEvent(Product product) {
        log.info("[Kafka] Received product-indexed event: id={}, name={}", product.getId(), product.getName());
        try {
            productRepository.save(product);
            log.info("[Kafka] Product saved to MySQL: id={}", product.getId());
        } catch (Exception e) {
            log.error("[Kafka] Failed to save product: id={}, error={}", product.getId(), e.getMessage());
        }
    }
}
