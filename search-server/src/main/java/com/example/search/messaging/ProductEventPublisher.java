package com.example.search.messaging;

import com.example.search.domain.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private static final String TOPIC = "product-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 상품이 ES에 색인될 때 Kafka로 이벤트 발행
     * batch-server가 이 이벤트를 소비해 MySQL에 저장함
     * (원본 ElasticBatchConfig: ES → MySQL sync 의 MSA 분리 버전)
     */
    public void publishProductIndexed(Product product) {
        log.info("[Kafka] Sending product-indexed event: id={}", product.getId());
        kafkaTemplate.send(TOPIC, product.getId(), product);
    }
}
