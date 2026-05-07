package com.example.search.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "product")
class Product(
    @Id
    var id: String? = null,
    var name: String? = null,
    var price: Double? = null,
    var category: String? = null
)
