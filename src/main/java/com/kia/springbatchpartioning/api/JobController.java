package com.kia.springbatchpartioning.api;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job job;

    public JobController(JobLauncher jobLauncher, @Qualifier("partitionedJob") Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("jobStartTime", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(job, jobParameters);
            return ResponseEntity.ok("Batch job started.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to start batch job.");
        }
    }
}
