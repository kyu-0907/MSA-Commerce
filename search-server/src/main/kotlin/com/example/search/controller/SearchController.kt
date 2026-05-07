package com.example.search.controller

import com.example.search.domain.Product
import com.example.search.messaging.ProductEventPublisher
import com.example.search.repository.ProductSearchRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/search")
class SearchController(
    private val productSearchRepository: ProductSearchRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val productEventPublisher: ProductEventPublisher
) {

    @GetMapping
    fun search(@RequestParam keyword: String): ResponseEntity<List<Product>> {
        val cacheKey = "search:$keyword"

        @Suppress("UNCHECKED_CAST")
        val cached = redisTemplate.opsForValue().get(cacheKey) as? List<Product>
        if (cached != null) {
            return ResponseEntity.ok(cached)
        }

        val results = productSearchRepository.findByNameContaining(keyword)

        redisTemplate.opsForValue().set(cacheKey, results, 60, TimeUnit.SECONDS)

        return ResponseEntity.ok(results)
    }

    @GetMapping("/category/{category}")
    fun searchByCategory(@PathVariable category: String): ResponseEntity<List<Product>> {
        val results = productSearchRepository.findByCategory(category)
        return ResponseEntity.ok(results)
    }

    @PostMapping("/index")
    fun indexProduct(@RequestBody product: Product): ResponseEntity<Product> {
        val saved = productSearchRepository.save(product)
        productEventPublisher.publishProductIndexed(saved)
        return ResponseEntity.ok(saved)
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<*> {
        return ResponseEntity.ok(mapOf("status" to "UP", "service" to "search-server"))
    }
}
