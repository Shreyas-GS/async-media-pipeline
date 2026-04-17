package com.example.media_pipeline.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // Lombok annotation that generates Getters, Setters, and toString automatically
public class RenderJobRequest {

    @NotBlank(message = "Client ID cannot be blank")
    private String clientId;

    @NotBlank(message = "Scene description is required")
    private String sceneDescription;

    private String charactersInvolved;

    private String referenceImageUrl;

    private Boolean preserveConsistency = true; // Default to true

    private String resolution = "1080p"; // Default resolution
}