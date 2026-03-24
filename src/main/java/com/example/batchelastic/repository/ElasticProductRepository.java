package com.example.batchelastic.repository;

import com.example.batchelastic.domain.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticProductRepository extends ElasticsearchRepository<Product, String> {
}
