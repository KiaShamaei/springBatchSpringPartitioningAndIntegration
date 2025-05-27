package com.kia.springbatchpartioning.batch;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;


@Configuration
@EnableBatchProcessing
public class WorkerConfig {

    private final JobRepository jobRepository;
    private final TransactionManager transactionManager;

    public WorkerConfig(JobRepository jobRepository, TransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    // Slave step processes individual partitions
    @Bean
    public Step workerStep() {
        return new StepBuilder("workerStep", jobRepository)
                .tasklet((Tasklet) (contribution, chunkContext) -> {
                    ExecutionContext context = chunkContext.getStepContext().getStepExecution().getExecutionContext();
                    int partitionNumber = context.getInt("partitionNumber");
                    System.out.println("Processing partition: " + partitionNumber);
                    return RepeatStatus.FINISHED;
                }, (PlatformTransactionManager) transactionManager)
                .build();
    }

    // Integration flow to handle partition requests
    @Bean
    public IntegrationFlow workerFlow(@Qualifier("workerStep") Step workerStep) {
        return IntegrationFlow.from("requestChannel")
                .handle((payload, headers) -> {
                    System.out.println("Received partition: " + payload);
                    return payload;
                })
                .channel("replyChannel")
                .get();
    }
}
