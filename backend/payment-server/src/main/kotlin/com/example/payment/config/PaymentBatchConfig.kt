package com.example.payment.config

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class PaymentBatchConfig {

    private val log = LoggerFactory.getLogger(PaymentBatchConfig::class.java)

    @Bean
    fun simplePaymentJob(jobRepository: JobRepository, simplePaymentStep: Step): Job {
        return JobBuilder("simplePaymentJob", jobRepository)
            .start(simplePaymentStep)
            .build()
    }

    @Bean
    fun simplePaymentStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("simplePaymentStep", jobRepository)
            .tasklet({ _, _ ->
                log.info(">>>>> This is simplePaymentStep tasklet")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
}
