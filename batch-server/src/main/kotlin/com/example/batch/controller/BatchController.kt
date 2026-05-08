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
    fun runJob(): ResponseEntity<*> = TODO()

    @GetMapping("/health")
    fun health(): ResponseEntity<*> = TODO()
}
