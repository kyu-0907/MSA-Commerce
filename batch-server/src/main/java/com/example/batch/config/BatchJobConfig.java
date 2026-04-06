package com.example.batch.config;

import com.example.batch.domain.Product;
import com.example.batch.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BatchJobConfig {

    private final ProductRepository productRepository;

    @Bean
    public Job sampleProductJob(JobRepository jobRepository, Step sampleProductStep) {
        return new JobBuilder("sampleProductJob", jobRepository)
                .start(sampleProductStep)
                .build();
    }

    @Bean
    public Step sampleProductStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sampleProductStep", jobRepository)
                .<Product, Product>chunk(10, transactionManager)
                .reader(sampleProductReader())
                .writer(productWriter())
                .build();
    }

    /**
     * 더미 데이터 생성 리더 (실습용)
     */
    @Bean
    public ItemReader<Product> sampleProductReader() {
        List<Product> sampleData = Arrays.asList(
                new Product("1", "Product A", 10000.0, "electronics"),
                new Product("2", "Product B", 20000.0, "fashion"),
                new Product("3", "Product C", 30000.0, "electronics"),
                new Product("4", "Product D", 15000.0, "food"),
                new Product("5", "Product E", 25000.0, "fashion")
        );

        return new ItemReader<Product>() {
            private Iterator<Product> iterator = sampleData.iterator();

            @Override
            public Product read() {
                return iterator.hasNext() ? iterator.next() : null;
            }
        };
    }

    @Bean
    public org.springframework.batch.item.ItemWriter<Product> productWriter() {
        return chunk -> productRepository.saveAll(chunk.getItems());
    }
}
