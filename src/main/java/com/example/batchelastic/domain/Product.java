package com.example.batchelastic.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.elasticsearch.annotations.Document;

@Entity
@Table(name = "product")
@Document(indexName = "product")
public class Product {
    @Id
    @org.springframework.data.annotation.Id
    private String id;
    private String name;
    private Double price;
    private String category;

    public Product() {}

    public Product(String id, String name, Double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
