package com.example.batch.config

import com.example.batch.domain.Product
import com.example.batch.repository.ProductRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class BatchJobConfig(
    private val productRepository: ProductRepository
) {

    @Bean
    fun sampleProductJob(jobRepository: JobRepository, sampleProductStep: Step): Job {
        return JobBuilder("sampleProductJob", jobRepository)
            .start(sampleProductStep)
            .build()
    }

    @Bean
    fun sampleProductStep(jobRepository: JobRepository, transactionManager: PlatformTransactionManager): Step {
        return StepBuilder("sampleProductStep", jobRepository)
            .chunk<Product, Product>(10, transactionManager)
            .reader(sampleProductReader())
            .writer(productWriter())
            .build()
    }

    @Bean
    fun sampleProductReader(): ItemReader<Product> {
        val sampleData = listOf(
            Product("1", "Product A", 10000.0, "electronics"),
            Product("2", "Product B", 20000.0, "fashion"),
            Product("3", "Product C", 30000.0, "electronics"),
            Product("4", "Product D", 15000.0, "food"),
            Product("5", "Product E", 25000.0, "fashion")
        )

        return object : ItemReader<Product> {
            private val iterator = sampleData.iterator()
            override fun read(): Product? {
                return if (iterator.hasNext()) iterator.next() else null
            }
        }
    }

    @Bean
    fun productWriter(): ItemWriter<Product> {
        return ItemWriter { chunk ->
            productRepository.saveAll(chunk.items)
        }
    }
}
