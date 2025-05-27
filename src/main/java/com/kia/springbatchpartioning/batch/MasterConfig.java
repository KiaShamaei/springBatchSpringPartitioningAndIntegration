package com.kia.springbatchpartioning.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.partition.MessageChannelPartitionHandler;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PriorityChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class MasterConfig {


    private final JobRepository jobRepository;

    public MasterConfig(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Step to split the job into partitions
    @Bean
    public Step partitioningStep(PartitionHandler partitionHandler) {
        return new StepBuilder("partitioningStep", jobRepository)
                .partitioner("workerStep", partitioner())
                .partitionHandler(partitionHandler)
                .build();
    }

    @Bean
    public Job partitionedJob(Step partitioningStep) {
        return new JobBuilder("partitionedJob", jobRepository)
                .start(partitioningStep)
                .build();
    }

    @Bean
    public Partitioner partitioner() {
        return gridSize -> {
            Map<String, ExecutionContext> partitions = new HashMap<>();
            int start = 1;
            int end = 10;

            for (int i = 0; i < gridSize; i++) {
                ExecutionContext context = new ExecutionContext();
                context.putInt("start", start);
                context.putInt("end", end);
                partitions.put("partition" + i, context);

                start += 10; // Increment start
                end += 10;   // Increment end
            }
            return partitions;
        };
    }

    @Bean
    public MessageChannel requestChannel() {
        return new DirectChannel();
    }

    @Bean
    public PollableChannel replyChannel() {
        return new PriorityChannel();
    }


    // PartitionHandler for sending partition data to workers
    @Bean
    public PartitionHandler partitionHandler(MessageChannel requestChannel, PollableChannel replyChannel) {
        // Create a MessagingTemplate
        MessagingTemplate messagingTemplate = new MessagingTemplate();
        messagingTemplate.setDefaultDestination(requestChannel); // Set the default destination channel

        // Configure the MessageChannelPartitionHandler
        MessageChannelPartitionHandler handler = new MessageChannelPartitionHandler();
        handler.setStepName("workerStep"); // The step name in the slave
        handler.setGridSize(3);           // Process 3 partitions at a time
        handler.setMessagingOperations(messagingTemplate); // Set the messaging template
        handler.setReplyChannel(replyChannel);             // Set the reply channel
        return handler;
    }
};

