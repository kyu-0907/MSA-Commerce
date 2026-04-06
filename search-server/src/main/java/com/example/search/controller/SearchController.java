package com.example.search.controller;

import com.example.search.domain.Product;
import com.example.search.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final ProductSearchRepository productSearchRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * GET /api/search?keyword=...
     * 키워드로 상품 검색 (Redis 캐시 적용)
     */
    @GetMapping
    public ResponseEntity<List<Product>> search(@RequestParam String keyword) {
        String cacheKey = "search:" + keyword;

        // Redis 캐시 확인
        @SuppressWarnings("unchecked")
        List<Product> cached = (List<Product>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        // ES 검색
        List<Product> results = productSearchRepository.findByNameContaining(keyword);

        // 캐시 저장 (TTL 60초)
        redisTemplate.opsForValue().set(cacheKey, results, 60, TimeUnit.SECONDS);

        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/search/category/{category}
     * 카테고리별 상품 조회
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> searchByCategory(@PathVariable String category) {
        List<Product> results = productSearchRepository.findByCategory(category);
        return ResponseEntity.ok(results);
    }

    /**
     * POST /api/search/index
     * 상품을 ES에 색인
     */
    @PostMapping("/index")
    public ResponseEntity<Product> indexProduct(@RequestBody Product product) {
        Product saved = productSearchRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    /**
     * GET /api/search/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(java.util.Map.of("status", "UP", "service", "search-server"));
    }
}
