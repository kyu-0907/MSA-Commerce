package com.example.search.repository

import com.example.search.domain.Product
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductSearchRepository : ElasticsearchRepository<Product, String> {
    fun findByNameContaining(name: String): List<Product>
    fun findByCategory(category: String): List<Product>
}
