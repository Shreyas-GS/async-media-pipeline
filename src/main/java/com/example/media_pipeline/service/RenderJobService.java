package com.example.media_pipeline.service;

import com.example.media_pipeline.config.RabbitMQConfig;
import com.example.media_pipeline.dto.RenderJobRequest;
import com.example.media_pipeline.entity.JobMetadata;
import com.example.media_pipeline.entity.RenderJob;
import com.example.media_pipeline.entity.RenderStatus;
import com.example.media_pipeline.repository.RenderJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor // Lombok magic: creates a constructor for our final fields automatically
@Slf4j // Lombok magic: gives us a logging object named 'log'
public class RenderJobService {

    private final RenderJobRepository renderJobRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional // Ensures if RabbitMQ crashes, the database save is rolled back!
    public UUID submitRenderJob(RenderJobRequest request) {

        log.info("Received new render job request for client: {}", request.getClientId());

        // 1. Create the Core Job Record
        RenderJob job = RenderJob.builder()
                .clientId(request.getClientId())
                .status(RenderStatus.PENDING)
                .build();

        // 2. Create the Metadata Record
        JobMetadata metadata = JobMetadata.builder()
                .renderJob(job)
                .sceneDescription(request.getSceneDescription())
                .charactersInvolved(request.getCharactersInvolved())
                .referenceImageUrl(request.getReferenceImageUrl())
                .preserveConsistency(request.getPreserveConsistency())
                .resolution(request.getResolution())
                .build();

        // Link metadata to the job
        job.setMetadata(metadata);

        // 3. Save to PostgreSQL (Because of CascadeType.ALL, this saves metadata too)
        RenderJob savedJob = renderJobRepository.save(job);

        // 4. Publish to RabbitMQ
        // We only send the UUID to the queue to keep the message lightweight
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                savedJob.getId().toString()
        );

        log.info("Successfully queued render job with ID: {}", savedJob.getId());

        return savedJob.getId();
    }
}
