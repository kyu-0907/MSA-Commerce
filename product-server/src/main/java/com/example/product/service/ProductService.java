package com.example.product.service;

import com.example.product.dto.ProductDto;
import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + id));
        return toDto(product);
    }

    public List<ProductDto> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(dto.getCategory())
                .build();
        Product saved = productRepository.save(product);
        kafkaTemplate.send("product-created", "product-id:" + saved.getId());
        return toDto(saved);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + id));
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());
        return toDto(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        kafkaTemplate.send("product-deleted", "product-id:" + id);
    }

    @Transactional
    public void decreaseStock(Long id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + id));
        if (product.getStock() < quantity) {
            throw new RuntimeException("재고가 부족합니다. 현재 재고: " + product.getStock());
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    private ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .build();
    }
}
