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
    fun search(@RequestParam keyword: String): ResponseEntity<List<Product>> = TODO()

    @GetMapping("/category/{category}")
    fun searchByCategory(@PathVariable category: String): ResponseEntity<List<Product>> = TODO()

    @PostMapping("/index")
    fun indexProduct(@RequestBody product: Product): ResponseEntity<Product> = TODO()

    @GetMapping("/health")
    fun health(): ResponseEntity<*> = TODO()
}
