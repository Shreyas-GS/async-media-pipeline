package com.example.media_pipeline.worker;

import com.example.media_pipeline.config.RabbitMQConfig;
import com.example.media_pipeline.entity.RenderJob;
import com.example.media_pipeline.entity.RenderStatus;
import com.example.media_pipeline.repository.RenderJobRepository;
import com.example.media_pipeline.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RenderJobWorker {

    private final RenderJobRepository renderJobRepository;
    private final SseService sseService; // Inject the SSE Service

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    @Transactional
    public void processRenderJob(String jobIdString) {
        try {


            UUID jobId = UUID.fromString(jobIdString);
            Optional<RenderJob> jobOptional = renderJobRepository.findById(jobId);

            if (jobOptional.isEmpty()) return;
            RenderJob job = jobOptional.get();

            // 1. Mark as PROCESSING and alert the frontend
            job.setStatus(RenderStatus.PROCESSING);
            renderJobRepository.save(job);
            sseService.sendProgress(jobId, "UPDATE", Map.of("status", "PROCESSING", "progress", "10%"));

            // 2. Simulate heavy rendering
            Thread.sleep(4000);
            sseService.sendProgress(jobId, "UPDATE", Map.of("status", "PROCESSING", "progress", "50%"));
            Thread.sleep(4000);
            sseService.sendProgress(jobId, "UPDATE", Map.of("status", "PROCESSING", "progress", "80%"));
            Thread.sleep(2000);

            // 3. Mark as COMPLETED and send final payload
            job.setStatus(RenderStatus.COMPLETED);
            renderJobRepository.save(job);

            sseService.completeJob(jobId, Map.of(
                    "status", "COMPLETED",
                    "progress", "100%",
                    "download_url", "https://cdn.example.com/renders/scene_" + jobId + ".mp4"
            ));

        } catch (Exception e) {
            log.error("Failed to process job ID: {}", jobIdString, e);
        }
    }
}