package com.example.search

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SearchServerApplication

fun main(args: Array<String>) {
    runApplication<SearchServerApplication>(*args)
}
