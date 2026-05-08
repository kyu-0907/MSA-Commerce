package com.example.product.controller

import com.example.product.dto.ProductDto
import com.example.product.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductDto>> = TODO()

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductDto> = TODO()

    @GetMapping("/category/{category}")
    fun getProductsByCategory(@PathVariable category: String): ResponseEntity<List<ProductDto>> = TODO()

    @GetMapping("/search")
    fun searchProducts(@RequestParam keyword: String): ResponseEntity<List<ProductDto>> = TODO()

    @PostMapping
    fun createProduct(@RequestBody dto: ProductDto): ResponseEntity<ProductDto> = TODO()

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Long, @RequestBody dto: ProductDto): ResponseEntity<ProductDto> = TODO()

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> = TODO()

    @PatchMapping("/{id}/stock/decrease")
    fun decreaseStock(@PathVariable id: Long, @RequestParam quantity: Int): ResponseEntity<Void> = TODO()
}
