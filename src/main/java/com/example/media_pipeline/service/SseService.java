package com.example.media_pipeline.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseService {

    // A thread-safe map to hold our active network connections
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID jobId) {
        // Create an emitter with a 30-minute timeout
        SseEmitter emitter = new SseEmitter(1800000L);

        emitters.put(jobId, emitter);

        // Clean up the map if the connection closes, times out, or errors out
        emitter.onCompletion(() -> emitters.remove(jobId));
        emitter.onTimeout(() -> emitters.remove(jobId));
        emitter.onError((e) -> emitters.remove(jobId));

        try {
            // Send an immediate confirmation to the client that the stream is open
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Successfully connected to stream for job: " + jobId));
        } catch (IOException e) {
            emitters.remove(jobId);
        }

        return emitter;
    }

    public void sendProgress(UUID jobId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(jobId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                log.error("Failed to push update to client for job {}", jobId);
                emitters.remove(jobId);
            }
        }
    }

    public void completeJob(UUID jobId, Object finalData) {
        SseEmitter emitter = emitters.get(jobId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("COMPLETED").data(finalData));
                emitter.complete(); // This cleanly closes the HTTP connection!
            } catch (IOException e) {
                emitter.completeWithError(e);
            } finally {
                emitters.remove(jobId);
            }
        }
    }
}