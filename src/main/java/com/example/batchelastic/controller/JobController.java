package com.example.batchelastic.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job productElasticJob;

    public JobController(JobLauncher jobLauncher, Job productElasticJob) {
        this.jobLauncher = jobLauncher;
        this.productElasticJob = productElasticJob;
    }

    @PostMapping("/run-job")
    public String runJob() {
        try {
            jobLauncher.run(productElasticJob, new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters());
            return "Job launched successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Job failed: " + e.getMessage();
        }
    }
}
