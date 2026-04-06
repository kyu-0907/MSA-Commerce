package com.example.batch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job sampleProductJob;

    /**
     * POST /api/batch/run
     * 배치 잡 실행
     */
    @PostMapping("/run")
    public ResponseEntity<?> runJob() {
        try {
            jobLauncher.run(sampleProductJob, new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters());
            return ResponseEntity.ok(Map.of("status", "STARTED", "message", "Job launched successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "FAILED", "error", e.getMessage()));
        }
    }

    /**
     * GET /api/batch/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "batch-server"));
    }
}
