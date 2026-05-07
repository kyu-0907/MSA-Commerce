package com.example.batch.controller

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/batch")
class BatchController(
    private val jobLauncher: JobLauncher,
    private val sampleProductJob: Job
) {

    @PostMapping("/run")
    fun runJob(): ResponseEntity<*> {
        return try {
            jobLauncher.run(
                sampleProductJob,
                JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters()
            )
            ResponseEntity.ok(mapOf("status" to "STARTED", "message" to "Job launched successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("status" to "FAILED", "error" to e.message))
        }
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<*> {
        return ResponseEntity.ok(mapOf("status" to "UP", "service" to "batch-server"))
    }
}
