package com.example.batchelastic.config;

import com.example.batchelastic.domain.Product;
import com.example.batchelastic.repository.ElasticProductRepository;
import com.example.batchelastic.repository.JpaProductRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ElasticBatchConfig {

    private final ElasticProductRepository elasticProductRepository;
    private final JpaProductRepository jpaProductRepository;

    public ElasticBatchConfig(ElasticProductRepository elasticProductRepository, JpaProductRepository jpaProductRepository) {
        this.elasticProductRepository = elasticProductRepository;
        this.jpaProductRepository = jpaProductRepository;
    }

    @Bean
    public Job productElasticJob(JobRepository jobRepository, Step productElasticStep) {
        return new JobBuilder("productElasticJob", jobRepository)
                .start(productElasticStep)
                .build();
    }

    @Bean
    public Step productElasticStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("productElasticStep", jobRepository)
                .<Product, Product>chunk(10, transactionManager)
                .reader(productReader())
                .writer(productWriter())
                .build();
    }

    @Bean
    public ItemReader<Product> productReader() {
        return new ItemReader<Product>() {
            private java.util.Iterator<Product> iterator = null;

            @Override
            public Product read() throws Exception {
                if (iterator == null) {
                    iterator = elasticProductRepository.findAll().iterator();
                }
                if (iterator.hasNext()) {
                    return iterator.next();
                }
                return null;
            }
        };
    }

    @Bean
    public org.springframework.batch.item.ItemWriter<Product> productWriter() {
        return chunk -> {
            List<Product> list = new ArrayList<>();
            chunk.getItems().forEach(list::add);
            jpaProductRepository.saveAll(list);
        };
    }
}
