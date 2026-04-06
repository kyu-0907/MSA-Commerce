package com.example.search.repository;

import com.example.search.domain.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<Product, String> {
    List<Product> findByNameContaining(String name);
    List<Product> findByCategory(String category);
}
