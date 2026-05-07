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
    fun getAllProducts(): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getAllProducts())
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.getProduct(id))
    }

    @GetMapping("/category/{category}")
    fun getProductsByCategory(@PathVariable category: String): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getProductsByCategory(category))
    }

    @GetMapping("/search")
    fun searchProducts(@RequestParam keyword: String): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.searchProducts(keyword))
    }

    @PostMapping
    fun createProduct(@RequestBody dto: ProductDto): ResponseEntity<ProductDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(dto))
    }

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Long, @RequestBody dto: ProductDto): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.updateProduct(id, dto))
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/stock/decrease")
    fun decreaseStock(@PathVariable id: Long, @RequestParam quantity: Int): ResponseEntity<Void> {
        productService.decreaseStock(id, quantity)
        return ResponseEntity.ok().build()
    }
}
