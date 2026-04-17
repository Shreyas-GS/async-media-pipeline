package com.example.media_pipeline.controller;

import com.example.media_pipeline.dto.RenderJobRequest;
import com.example.media_pipeline.service.RenderJobService;
import com.example.media_pipeline.service.SseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class RenderJobController {

    private final RenderJobService renderJobService;
    private final SseService sseService; // Inject our new SSE Service

    @PostMapping
    public ResponseEntity<Map<String, Object>> createJob(@Valid @RequestBody RenderJobRequest request) {
        UUID jobId = renderJobService.submitRenderJob(request);

        return ResponseEntity.accepted().body(Map.of(
                "message", "Render job accepted and queued for processing.",
                "job_id", jobId,
                "stream_url", "/api/v1/jobs/" + jobId + "/stream" // Give the client the stream URL
        ));
    }

    // NEW ENDPOINT: This creates the persistent streaming connection
    @GetMapping(value = "/{jobId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamJobStatus(@PathVariable UUID jobId) {
        return sseService.subscribe(jobId);
    }
}